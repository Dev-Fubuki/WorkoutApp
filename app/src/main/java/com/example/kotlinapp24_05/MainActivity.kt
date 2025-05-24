package com.example.kotlinapp24_05

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.*
import androidx.compose.ui.text.style.TextAlign

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaListaTreinos(
    viewModel: TreinoViewModel,
    aoAdicionar: () -> Unit,
    aoEditar: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = aoAdicionar,
                icon = { Icon(Icons.Default.Add, "Adicionar") },
                text = { Text("Novo Treino 🏋️") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(4.dp))
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "📋 Meus Treinos",
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 20.sp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { padding ->
        if (viewModel.listaTreinos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Nenhum treino cadastrado\nClique no + para começar! 🚀",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(top = 16.dp) // espaçamento adicional do topo
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.listaTreinos) { treino ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "🏆 ${treino.nome}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "🔄 Repetições: ${treino.repeticoes}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { aoEditar(treino.id) },
                                    modifier = Modifier.clip(MaterialTheme.shapes.small)
                                ) {
                                    Icon(
                                        Icons.Outlined.Edit,
                                        contentDescription = "Editar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.removerTreino(treino.id) },
                                    modifier = Modifier.clip(MaterialTheme.shapes.small)
                                ) {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = "Remover",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
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
            TopAppBar(
                title = {
                    Text(
                        if (id == null) "➕ Novo Treino" else "✏️ Editar Treino"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val novo = Treino(nome = nome.text, repeticoes = repeticoes.text)
                    if (id != null) viewModel.editarTreino(id, novo)
                    else viewModel.adicionarTreino(novo)
                    aoSalvar()
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Check, "Salvar", tint = MaterialTheme.colorScheme.primary)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "📝 Detalhes do Treino",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do exercício") },
                leadingIcon = { Icon(Icons.Outlined.FitnessCenter, "Exercício") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                ),
                placeholder = { Text("Ex: Supino reto... 💪") }
            )

            OutlinedTextField(
                value = repeticoes,
                onValueChange = { repeticoes = it },
                label = { Text("Repetições") },
                leadingIcon = { Icon(Icons.Outlined.Repeat, "Repetições") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                ),
                placeholder = { Text("Ex: 3x12 🔄") }
            )
        }
    }
}

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
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF00796B),
                    secondary = Color(0xFF004D40),
                    surface = Color(0xFFF5F5F5),
                    surfaceVariant = Color(0xFFE0E0E0),
                    onSurface = Color(0xFF212121),
                    onSurfaceVariant = Color(0xFF424242),
                    primaryContainer = Color(0xFF80CBC4),
                ),
                typography = Typography(
                    displaySmall = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = 0.5.sp
                    ),
                    titleLarge = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                ),
                shapes = Shapes(
                    small = RoundedCornerShape(8.dp),
                    medium = RoundedCornerShape(16.dp),
                    large = RoundedCornerShape(24.dp)
                )
            ) {
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
