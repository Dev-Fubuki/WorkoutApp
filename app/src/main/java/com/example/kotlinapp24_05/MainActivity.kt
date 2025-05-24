package com.example.kotlinapp24_05

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.*

// ================== MODELO E VIEWMODEL ==================

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

// ================== COMPONENTES VISUAIS ==================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTreinoCard(
    treino: Treino,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        ListItem(
            headlineContent = {
                Text(
                    treino.nome,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            supportingContent = {
                Text(
                    "Repetições: ${treino.repeticoes}",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingContent = {
                Icon(
                    Icons.Outlined.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp))
            },
            trailingContent = {
                Row {
                    IconButton(onClick = onEdit) {
                        Text(
                            text = "✏️",
                            fontSize = 20.sp
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Excluir",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TelaListaTreinos(
    viewModel: TreinoViewModel,
    navController: NavHostController
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var treinoToDelete by remember { mutableStateOf<Treino?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("novo") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Adicionar"
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "📋 Meus Treinos",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Nenhum treino cadastrado",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        "Toque no + para começar!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.listaTreinos, key = { it.id }) { treino ->
                    SwipeableTreinoCard(
                        treino = treino,
                        onEdit = { navController.navigate("editar/${treino.id}") },
                        onDelete = {
                            treinoToDelete = treino
                            showDeleteDialog = true
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar exclusão") },
            text = { Text("Deseja realmente excluir o treino ${treinoToDelete?.nome}?" ) },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        treinoToDelete?.id?.let { viewModel.removerTreino(it) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Excluir", color = MaterialTheme.colorScheme.onError)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFormularioTreino(
    viewModel: TreinoViewModel,
    id: String? = null,
    navController: NavHostController
) {
    val treinoExistente = id?.let { viewModel.buscarPorId(it) }
    var nome by remember { mutableStateOf(treinoExistente?.nome ?: "") }
    var repeticoes by remember { mutableStateOf(treinoExistente?.repeticoes ?: "") }

    var nomeError by remember { mutableStateOf(false) }
    var repeticoesError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (id == null) "➕ Novo Treino" else "✏️ Editar Treino",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    nomeError = nome.isBlank()
                    repeticoesError = repeticoes.isBlank()

                    if (!nomeError && !repeticoesError) {
                        val novo = Treino(
                            nome = nome.trim(),
                            repeticoes = repeticoes.trim()
                        )
                        if (id != null) viewModel.editarTreino(id, novo)
                        else viewModel.adicionarTreino(novo)
                        navController.popBackStack()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Salvar"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "📝 Detalhes do Treino",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            CampoFormulario(
                valor = nome,
                aoMudar = {
                    nome = it
                    if (nomeError) nomeError = false
                },
                rotulo = "Nome do exercício",
                icone = Icons.Outlined.FitnessCenter,
                placeholder = "Insira seu treino",
                isError = nomeError,
                mensagemErro = "Nome obrigatório",
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )

            CampoFormulario(
                valor = repeticoes,
                aoMudar = {
                    repeticoes = it
                    if (repeticoesError) repeticoesError = false
                },
                rotulo = "Repetições/Séries",
                icone = Icons.Outlined.Repeat,
                placeholder = "Insira suas repetições",
                isError = repeticoesError,
                mensagemErro = "Repetições obrigatórias",
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            )
        }
    }
}

@Composable
fun CampoFormulario(
    valor: String,
    aoMudar: (String) -> Unit,
    rotulo: String,
    icone: ImageVector,
    placeholder: String,
    isError: Boolean,
    mensagemErro: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.labelMedium,
            color = if (isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = valor,
            onValueChange = aoMudar,
            leadingIcon = { Icon(icone, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                focusedLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            ),
            placeholder = {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                )
            },
            singleLine = true,
            isError = isError,
            keyboardOptions = keyboardOptions,
            // SOLUÇÃO DEFINITIVA: Mostrar placeholder como texto de suporte
            supportingText = {
                if (valor.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )

        AnimatedVisibility(
            visible = isError,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = mensagemErro,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun NavegacaoApp(viewModel: TreinoViewModel = viewModel()) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {
        composable("lista") {
            TelaListaTreinos(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable("novo") {
            TelaFormularioTreino(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable("editar/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            TelaFormularioTreino(
                viewModel = viewModel,
                id = id,
                navController = navController
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF006A60),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFF83F0E0),
                    onPrimaryContainer = Color(0xFF00201C),
                    secondary = Color(0xFF4A635F),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFCCE8E1),
                    onSecondaryContainer = Color(0xFF05201B),
                    surface = Color(0xFFF0F4F3),
                    onSurface = Color(0xFF191C1B),
                    surfaceVariant = Color(0xFFE8EFEC),
                    onSurfaceVariant = Color(0xFF3F4946),
                    error = Color(0xFFBA1A1A),
                    errorContainer = Color(0xFFFFDAD6)
                ),
                typography = Typography(
                    displaySmall = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = 0.5.sp
                    ),
                    headlineSmall = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    ),
                    titleLarge = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    bodyLarge = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                ),
                shapes = Shapes(
                    extraSmall = RoundedCornerShape(4.dp),
                    small = RoundedCornerShape(8.dp),
                    medium = RoundedCornerShape(16.dp),
                    large = RoundedCornerShape(24.dp),
                    extraLarge = RoundedCornerShape(32.dp)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    NavegacaoApp()
                }
            }
        }
    }
}