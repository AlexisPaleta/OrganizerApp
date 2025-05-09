package com.fcc.organizador.schedule

import androidx.lifecycle.ViewModel

class ScheduleViewModel: ViewModel() {
    private var columnsCount = 3

    fun getColumnsCount(): Int{
        return columnsCount
    }

    fun setColumnsCount(count: Int){
        columnsCount = count
    }
}