package com.fcc.organizador

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TeacherViewModel: ViewModel() {
    private var newTeacher: MutableLiveData<Teacher?> = MutableLiveData()
    private var editing = false //This variable will be checked when the FullScreenDialogTeacherFragment was called, if the floating
    //button was pressed then editing will be false, but if the teacherItem is swiped to the right then editing will be true
    private var editingTeacher: Teacher? = null //This teacher object is for the edit logic
    private var editTeacher:MutableLiveData<Teacher?> = MutableLiveData() //This MutableLiveData teacher is for the edit logic
    //an observer will be save the teacher value saved here
    private var editedPosition: Int = 0

    fun getNewTeacher(): MutableLiveData<Teacher?>{
        return newTeacher
    }

    fun teacherAdded(){//This function is used by the FullScreenDialogTeacherFragment when the teacher is correctly added
        //it is to make the observer know that it doesn't have to add a new teacher until the MutableLiveData is not null
        newTeacher.value = null
    }

    fun setNewTeacher(t: Teacher){
        newTeacher.value = t
    }

    fun getEditing(): Boolean{
        return editing
    }

    fun setEditing(value: Boolean){
        editing = value
    }

    fun getEditingTeacher(): Teacher?{
        return editingTeacher
    }

    fun setEditingTeacher(teacher: Teacher){
        editingTeacher = teacher
    }

    fun getEditTeacher(): MutableLiveData<Teacher?>{
        return editTeacher
    }

    fun teacherEdited(){//This function is used by the FullScreenDialogTeacherFragment when the teacher is correctly added
        //it is to make the observer know that it doesn't have to add a new teacher until the MutableLiveData is not null
        editTeacher.value = null
    }

    fun setEditTeacher(t: Teacher){
        editTeacher.value = t
    }

    fun getEditedPosition(): Int{
        return editedPosition
    }

    fun setEditedPosition(position: Int){
        editedPosition = position
    }

}