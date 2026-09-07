package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppSettings
import com.example.data.Note
import com.example.data.NoteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteEditViewModel(
    private val noteDao: NoteDao,
    private val appSettings: AppSettings
) : ViewModel() {

    private var currentNoteId: Int? = null

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _subtitle = MutableStateFlow("")
    val subtitle: StateFlow<String> = _subtitle.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()
    
    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    fun loadNote(noteId: Int?) {
        currentNoteId = noteId
        if (noteId != null) {
            viewModelScope.launch {
                val note = noteDao.getNoteById(noteId).first()
                if (note != null) {
                    _title.value = note.title
                    _subtitle.value = note.subtitle
                    _content.value = note.content
                }
            }
        } else {
            _title.value = ""
            _subtitle.value = ""
            _content.value = ""
        }
    }

    fun updateTitle(t: String) { _title.value = t }
    fun updateSubtitle(s: String) { _subtitle.value = s }
    fun updateContent(c: String) { _content.value = c }

    fun saveNote() {
        viewModelScope.launch {
            val userId = appSettings.currentUserId.first() ?: return@launch
            val note = Note(
                id = currentNoteId ?: 0,
                userId = userId,
                title = _title.value,
                subtitle = _subtitle.value,
                content = _content.value,
                timestamp = System.currentTimeMillis()
            )
            if (currentNoteId == null) {
                noteDao.insertNote(note)
            } else {
                noteDao.updateNote(note)
            }
            _isSaved.value = true
        }
    }
}
