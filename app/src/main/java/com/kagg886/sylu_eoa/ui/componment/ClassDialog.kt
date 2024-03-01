package com.kagg886.sylu_eoa.ui.componment

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kagg886.sylu_eoa.api.v2.bean.ClassUnit

@Composable
fun ClassDialog(unit: ClassUnit, dialog: Boolean, onDismiss: () -> Unit) {
    if (dialog) {
        AlertDialog(onDismissRequest = onDismiss, confirmButton = {},
            title = {
                Text("课程 ${unit.name} 详情")
            },
            text = {
                Column {
                    Details("上课时间", unit.weekEachLesson)
                    Details("教室", unit.room)
                    Details("老师", unit.teacher)
                    Details("考试形式", unit.classType)
                    Details("学分", unit.score)
                }
            }
        )
    }
}