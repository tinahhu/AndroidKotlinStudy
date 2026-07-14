package com.example.demo1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Serializable
data class TodoItem (
    val id: Int,
    val content: String,
    val isCompleted: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoList()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoList() {

    var inputText by remember { mutableStateOf("") }
    var nextId by remember { mutableStateOf(1) }
    val todoList = remember { mutableStateListOf<TodoItem>() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("TO DO LIST") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                // 用于修改左右两侧的padding间距
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("输入代办", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        todoList.add(TodoItem(nextId++, inputText))
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("添加")
            }
            /**
             * 放了一个间隔块在添加和list中间，高度为20dp
             */
            Spacer(modifier = Modifier.height(20.dp))

            if (todoList.isEmpty()) {
                Text("暂无代办事项", color = Color.Gray)
            } else {
                /**
                 * 懒加载，只渲染可见项，自带垂直滚动
                 */
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(todoList, key = { it.id }) { item ->
                        TodoItemRow(
                            item = item,
                            onDelete = {
                                todoList.remove(item)
                            },
                            onDone = {
                                /**
                                 * indexOfFirst 用于遍历列表，找到第一个满足大括号里条件的元素；
                                 * 返回这个元素在列表中的下标；
                                 * 如果没有找到满足条件的元素，就返回-1
                                 */
                                val index = todoList.indexOfFirst { it.id == item.id }
                                if (index >= 0) {
                                    todoList[index] = item.copy(isCompleted = !item.isCompleted)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItemRow(
    item: TodoItem,
    onDelete: () -> Unit,
    onDone: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            /**
             * 给水波纹切圆角
             */
            modifier = Modifier.clip(MaterialTheme.shapes.small),
            onClick = onDone
        ) {
            Icon(
                imageVector = if (item.isCompleted) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = "完成"
            )
        }

        Text(
            text = item.content,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            color = if (item.isCompleted) Color.Gray else Color.Black,
            textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
        )

        IconButton(
            modifier = Modifier.clip(MaterialTheme.shapes.small),
            onClick = onDelete
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "删除"
            )
        }
    }
}