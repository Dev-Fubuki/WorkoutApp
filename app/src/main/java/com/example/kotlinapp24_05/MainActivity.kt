package com.example.kotlinapp24_05

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.*


data class Treino(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val repeticoes: String
)


class TreinoViewModel : ViewModel() {
    var listaTreinos by mutableStateOf(listOf<Treino>())
        private set

    fun adicionarTreino(treino: Treino) {
        listaTreinos = listaTreinos + treino
    }

    fun editarTreino(id: String, novoTreino: Treino) {
        listaTreinos = listaTreinos.map {
            if (it.id == id) novoTreino.copy(id = id) else it
        }
    }

    fun removerTreino(id: String) {
        listaTreinos = listaTreinos.filterNot { it.id == id }
    }

    fun buscarPorId(id: String): Treino? = listaTreinos.find { it.id == id }
}

// TELA PRINCIPAL
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaListaTreinos(
    viewModel: TreinoViewModel,
    aoAdicionar: () -> Unit,
    aoEditar: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = aoAdicionar) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        },
        topBar = {
            TopAppBar(title = { Text("Meus Treinos") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(viewModel.listaTreinos) { treino ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(treino.nome, style = MaterialTheme.typography.titleMedium)
                            Text("Repetições: ${treino.repeticoes}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row {
                            IconButton(onClick = { aoEditar(treino.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { viewModel.removerTreino(treino.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFormularioTreino(
    viewModel: TreinoViewModel,
    id: String? = null,
    aoSalvar: () -> Unit
) {
    val treinoExistente = id?.let { viewModel.buscarPorId(it) }
    var nome by remember { mutableStateOf(TextFieldValue(treinoExistente?.nome ?: "")) }
    var repeticoes by remember { mutableStateOf(TextFieldValue(treinoExistente?.repeticoes ?: "")) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (id == null) "Adicionar Treino" else "Editar Treino") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val novo = Treino(nome = nome.text, repeticoes = repeticoes.text)
                if (id != null) viewModel.editarTreino(id, novo)
                else viewModel.adicionarTreino(novo)
                aoSalvar()
            }) {
                Icon(Icons.Default.Check, contentDescription = "Salvar")
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do exercício") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = repeticoes,
                onValueChange = { repeticoes = it },
                label = { Text("Repetições") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// NAVEGAÇÃO
@Composable
fun NavegacaoApp(viewModel: TreinoViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "lista") {
        composable("lista") {
            TelaListaTreinos(
                viewModel = viewModel,
                aoAdicionar = { navController.navigate("novo") },
                aoEditar = { id -> navController.navigate("editar/$id") }
            )
        }
        composable("novo") {
            TelaFormularioTreino(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable("editar/{id}") { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            TelaFormularioTreino(viewModel = viewModel, id = id) {
                navController.popBackStack()
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavegacaoApp(viewModel = viewModel())
                }
            }
        }
    }
}
