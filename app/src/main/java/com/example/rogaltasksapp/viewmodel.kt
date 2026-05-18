package com.example.rogaltasksapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


data class UiState(
    val isLoading: Boolean = true,
    val isHarmoLoading: Boolean = true,
    val zadania: List<Pair<Task, List<Child>>> = emptyList(),
    var wpisyHarmo: List<Harmonogram> = emptyList(),
    val errors: String? = null,
    val info: String? = null,
    val ID: Int = 0,
    val internet: Boolean = true
)

@HiltViewModel
class TaskViewModel @Inject constructor(val repository: ZadaniaRepository, val settingsRepo: SettingsRepository, private val internetConnection: InternetConnection, val daoRepo: DaoRepository) : ViewModel()
{


    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var pollingJob: Job? = null

    init {

        viewModelScope.launch{
            internetConnection.connected.collect{
                    value -> _uiState.update{it.copy(internet=value)}
            }
        }
        viewModelScope.launch{
            settingsRepo.loginFlow.collect {id -> _uiState.update {it.copy(ID=id)}
                if (id!=0)
                {
                    getTasks("any")
                    getHarmo()
                    updateFCM(id)
                }

            }
        }
        pollingJob = viewModelScope.launch{
            while (isActive)
            {
                if (uiState.value.ID!=0)
                    getTasks("any")
                delay(5 * 60 * 1000L)
            }

        }
    }

    private suspend fun updateFCM(id:Int)
    {
        if (uiState.value.internet)
            repository.updateFCM(id, Firebase.messaging.token.await())
    }
    private suspend fun updateDAO()
    {
        try
        {
            val res = repository.getTasksBasic(uiState.value.ID)
            daoRepo.syncTasks(res)

        }
        catch (e:Exception)
        {
            _uiState.update{it.copy(errors = "Błąd: ${e.message}")}
        }
    }
    private suspend fun getTasks(data:String)
    {
        _uiState.update{it.copy(isLoading = true)}
        if (_uiState.value.internet)
            try
            {
                val gson = Gson()
                val response = repository.getTasks(uiState.value.ID, data)
                val tasks = response.map { task ->
                    val childrenList: List<Child> =
                        gson.fromJson(task.children, Array<Child>::class.java).toList()

                    task to childrenList
                }
                updateDAO()

                delay(500)
                _uiState.update{it.copy(isLoading = false, zadania = tasks)}
            }
            catch(e: Exception)
            {
                _uiState.update{it.copy(isLoading = false, errors = "Błąd: ${e.message}")}
            }
        else
        {
            val results = daoRepo.getTasks(uiState.value.ID).map{ item-> val children = item.children
                val ratio = if (children.isNotEmpty()){
                    children.map {it.status}.average()
                } else 0.0
                Task( item.parent.ID, item.parent.nazwa, item.parent.data,"", ratio, item.parent.parentID) to children.map {
                    Child(it.ID, it.data, it.nazwa, it.status)
                }
            }

            _uiState.update{it.copy(isLoading = false, zadania=results)}
        }
    }
    // Pozniej mozna dodac taka opcje ze na ekranie bedzie napis tego co zwraca api
    fun addTask(req: AddTaskPOST)
    {
        viewModelScope.launch {
            if (uiState.value.internet)
                try {
                    val response = repository.addTask(uiState.value.ID, req)
                    delay(200)
                    getTasks("any")

                } catch (e: Exception) {
                    Log.e("API", "Exception: ${e.message}")
                }

        }
    }
    fun deleteTaskLocal(taskId: Int) {
        _uiState.update { state ->
            val updated = state.zadania.filter { it.first.ID != taskId }
            state.copy(zadania = updated)
        }
    }
    fun removeChildFromParentLocal(taskId: Int, parentID:Int) {
        _uiState.update { state ->
            val updated = state.zadania.map { (task, children) ->
                if (task.ID == parentID) {
                    Log.e("API", children.size.toString())
                    val newChildren = children.filter { it.ID != taskId }
                    Log.e("API", newChildren.size.toString())
                    task to newChildren
                } else {
                    task to children
                }
            }
            state.copy(zadania = updated)
        }
    }
    fun deleteTask(id:Int, par:Int=0)
    {
        viewModelScope.launch{
            try {
                val response = repository.deleteTask(id)
                delay(200)
                if (par!=0)
                    removeChildFromParentLocal(id, par)
                else
                    deleteTaskLocal(id)

            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }
    fun finishTask(id:Int, par:Int=0)
    {
        viewModelScope.launch{
            try {
                val response = repository.finishTask(id)
                delay(200)
                if (par!=0)
                    removeChildFromParentLocal(id, par)
                else
                    deleteTaskLocal(id)

            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }

    fun login(login : String, haslo:String)
    {
        viewModelScope.launch{
            val post = LoginPOST(login, haslo)
            try{
                val response = repository.login(post)
                if (response.isSuccessful)
                {
                    _uiState.update{state -> state.copy(ID = response.body()?.dane?:0)}
                    settingsRepo.setLogin(uiState.value.ID)
                    getTasks( "any")
                }
                else
                {
                    val err = response.errorBody()?.string()
                    val gson = Gson()
                    val error = gson.fromJson(err, ResponseFromServer::class.java)
                    _uiState.update{state -> state.copy(info = error?.message)}
                }

            }
            catch (e: Exception)
            {
                Log.e("API", "Exception: ${e.message}")
            }

        }


    }
    fun logout()
    {
        viewModelScope.launch {
            repository.updateFCM(_uiState.value.ID, "")
            _uiState.update { state -> state.copy(ID = 0) }
            settingsRepo.setLogin(uiState.value.ID)
        }
    }

    fun register(login : String, haslo:String)
    {
        viewModelScope.launch{
            val post = LoginPOST(login, haslo)
            try{
                val response = repository.register(post)
                if (response.isSuccessful)
                {
                    val responseLog = repository.login(post)
                    _uiState.update{state -> state.copy(ID = responseLog.body()?.dane?:0)}
                    getTasks("any")
                }
                else
                {
                    val err = response.errorBody()?.string()
                    val gson = Gson()
                    val error = gson.fromJson(err, ResponseFromServer::class.java)
                    _uiState.update{state -> state.copy(info = error?.message)}
                }

            }
            catch (e: Exception)
            {
                Log.e("API", "Exception: ${e.message}")
            }

        }


    }

    fun getHarmo()
    {
        viewModelScope.launch{
            if (uiState.value.internet)
            {
                _uiState.update{it.copy(isHarmoLoading = true)}
                try{

                    val response = repository.getHarmo(uiState.value.ID)
                    _uiState.update { state-> state.copy(wpisyHarmo = response.harmonogram) }
                    try{
                        daoRepo.syncHarmo(response.harmonogram)
                    }
                    catch(e1: Exception)
                    {
                        Log.e("DAOSync", "Exception: ${e1.message}")
                    }
                }
                catch (e: Exception)
                {
                    Log.e("HARMONOGRAM", "Exception: ${e.message}")
                }
            }
            else
                try{
                    val re = daoRepo.getHarmo(uiState.value.ID)
                    _uiState.update { state-> state.copy(wpisyHarmo = re) }
                }
                catch (e: Exception)
                {
                    Log.e("DAO", "Exception: ${e.message}")
                }
            _uiState.update{it.copy(isHarmoLoading = false)}
        }
    }

    fun addHarmo(request: HarmoPOST)
    {
        viewModelScope.launch{
            _uiState.update{it.copy(isHarmoLoading = true)}
            try{
                val response = repository.addHarmo(uiState.value.ID, request)
                getHarmo()
            }
            catch (e: Exception)
            {
                Log.e("HARMONOGRAM", "Exception: ${e.message}")
            }
            _uiState.update{it.copy(isHarmoLoading = false)}
        }
    }
    fun editHarmo(request: HarmoPOST, harmoID: Int)
    {
        viewModelScope.launch{
            _uiState.update{it.copy(isHarmoLoading = true)}
            try{
                val response = repository.editHarmo(harmoID, request)
                getHarmo()
            }
            catch (e: Exception)
            {
                Log.e("HARMONOGRAM", "Exception: ${e.message}")
            }
            _uiState.update{it.copy(isHarmoLoading = false)}
        }
    }

}