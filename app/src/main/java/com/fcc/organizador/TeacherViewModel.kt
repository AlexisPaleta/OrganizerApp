package com.fcc.organizador

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TeacherViewModel: ViewModel() {
    private var teacher: MutableLiveData<Teacher?> = MutableLiveData()

    fun getTeacher(): MutableLiveData<Teacher?>{
        return teacher
    }

    fun teacherAdded(){//This function is used by the FullScreenDialogTeacherFragment when the teacher is correctly added
        //it is to make the observer know that it doesn't have to add a new teacher until the MutableLiveData is not null
        teacher.value = null
    }

    fun setTeacher(t: Teacher){
        teacher.value = t
    }

}