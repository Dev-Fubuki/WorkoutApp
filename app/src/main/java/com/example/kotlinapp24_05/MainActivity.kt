package com.example.kotlinapp24_05

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import java.util.UUID

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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    treino.nome,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            },
            supportingContent = {
                Text(
                    "Repetições: ${treino.repeticoes}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            },
            leadingContent = {
                Icon(
                    Icons.Outlined.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingContent = {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.secondary
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier
                    .shadow(8.dp, shape = CircleShape, spotColor = MaterialTheme.colorScheme.primary)
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
                        style = MaterialTheme.typography.displaySmall.copy(
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.tertiary
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
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Nenhum treino cadastrado",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Toque no + para começar!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
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
                        style = MaterialTheme.typography.displaySmall.copy(
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.tertiary,
                    navigationIconContentColor = MaterialTheme.colorScheme.tertiary
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
                containerColor = Color.Transparent,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Salvar",
                    tint = MaterialTheme.colorScheme.onPrimary
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
            leadingIcon = {
                Icon(
                    icone,
                    contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.secondary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                unfocusedIndicatorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                focusedLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                unfocusedLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                errorLeadingIconColor = MaterialTheme.colorScheme.error
            ),
            placeholder = {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            },
            singleLine = true,
            isError = isError,
            keyboardOptions = keyboardOptions,
            supportingText = {
                if (isError) {
                    Text(
                        text = mensagemErro,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        )
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
                colorScheme = darkColorScheme(
                    primary = Color(0xFFFF6B35),
                    onPrimary = Color(0xFF121212),
                    primaryContainer = Color(0xFFD85E2D),

                    secondary = Color(0xFF00C49A),
                    onSecondary = Color(0xFF000000),
                    secondaryContainer = Color(0xFF008C6D),

                    tertiary = Color(0xFFFFD166),
                    onTertiary = Color(0xFF000000),

                    background = Color(0xFF121212),
                    onBackground = Color(0xFFFFFFFF),

                    surface = Color(0xFF1E1E1E),
                    onSurface = Color(0xFFFFFFFF),
                    surfaceVariant = Color(0xFF2A2A2A),
                    onSurfaceVariant = Color(0xFFA0A0A0),

                    error = Color(0xFFFF4B4B),
                    onError = Color(0xFFFFFFFF),

                    outline = Color(0xFF404040),
                    outlineVariant = Color(0xFF00C49A)
                ),
                typography = Typography(
                    displaySmall = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFFFFD166)
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
                        lineHeight = 24.sp,
                        color = Color(0xFFA0A0A0)
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
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavegacaoApp()
                }
            }
        }
    }
}