package com.example.appfirabasefirestore

//Por: Luisa Santos Silva - 3°DS AMS

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appfirabasefirestore.ui.theme.AppFirabaseFirestoreTheme
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {

    // Instância do Firestore para permitir o acesso ao banco de dados Firebase
    val db = FirebaseFirestore.getInstance()

    // Constante para uso nos logs de depuração
    companion object {
        private const val TAG = "MainActivity"
    }

    // Função chamada ao iniciar a atividade.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Aplica o tema e exibe o Composable principal `App`
            AppFirabaseFirestoreTheme {
                App(db)  // Passa a instância do Firestore para o Composable `App`
            }
        }
    }

    // Função Composable principal que define a interface do usuário
    @Composable
    fun App(db: FirebaseFirestore) {
        // Variáveis mutáveis para armazenar os valores dos campos de entrada de nome e telefone
        var nome by remember { mutableStateOf("") }
        var telefone by remember { mutableStateOf("") }
        val lightPurple = Color((0xFFD1C4E9))

        // Lista observável de clientes, que será atualizada com dados do Firestore
        val clientes = remember { mutableStateListOf<Cliente>() }

        // Layout principal organizado em uma coluna
        Column(
            Modifier.fillMaxWidth()
        ) {
            // Título do aplicativo
            Text(
                text = "App Firebase Firestore",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = lightPurple) // Cor de fundo do título
                    .padding(8.dp)  // Espaço interno
            )

            // Espaçamento entre o título e os dados
            Row(Modifier.fillMaxWidth().padding(10.dp)) {}

            // Texto com nome e turma
            Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                Text(text = "Luisa Santos Silva - 3°DS AMS")
            }

            // Imagem personalizada
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.luisa),
                    contentDescription = "Luisa",
                    modifier = Modifier
                        .size(200.dp)
                        .border(
                            width = 2.dp,
                            color = lightPurple,  // Cor da borda
                        )
                        .padding(16.dp)
                )
            }

            // Espaçamento entre os dados e o formulário
            Row(Modifier.fillMaxWidth().padding(20.dp)) {}

            // Campo de entrada para o nome
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth(0.3f)) {
                    Text(text = "Nome:")  // Rótulo do campo
                }
                Column {
                    TextField(
                        value = nome,
                        onValueChange = { nome = it }
                    )
                }
            }

            // Campo de entrada para o telefone
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth(0.3f)) {
                    Text(text = "Telefone:")  // Rótulo do campo
                }
                Column {
                    TextField(
                        value = telefone,
                        onValueChange = { telefone = it }
                    )
                }
            }

            // Espaçamento entre os campos e o botão
            Row(Modifier.fillMaxWidth().padding(10.dp)) {}

            // Botão "Cadastrar" centralizado
            Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                Button(onClick = {
                    // Cria um HashMap para armazenar os dados inseridos e salva no Firestore
                    val pessoa = hashMapOf(
                        "nome" to nome,
                        "telefone" to telefone
                    )

                    // Adiciona os dados à coleção "Clientes" no Firestore
                    db.collection("Clientes").add(pessoa)
                        .addOnSuccessListener { documentReference ->
                            // Exibe uma mensagem de sucesso se a gravação for bem-sucedida
                            Log.d(TAG, "DocumentSnapshot successfully written with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            // Exibe uma mensagem de erro caso ocorra falha
                            Log.w(TAG, "Error writing document", e)
                        }
                }) {
                    Text(text = "Cadastrar")
                }
            }

            // Espaçamento entre o botão e o título de cada coluna da lista dos clientes
            Row(Modifier.fillMaxWidth().padding(20.dp)) {}

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Nome: ")
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Telefone: ")
                }
            }

            // Espaçamento entre os títulos e a lista
            Row(Modifier.fillMaxWidth().padding(5.dp)) {}

            // Carregar os dados da coleção "Clientes" uma vez, usando LaunchedEffect
            LaunchedEffect(Unit) {
                //snapshotListener para atualizações em tempo real
                db.collection("Clientes").addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    // Limpa a lista local e a atualiza com os dados mais recentes do Firestore
                    clientes.clear()
                    snapshots?.forEach { document ->
                        val cliente = Cliente(
                            nome = document.getString("nome") ?: "",
                            telefone = document.getString("telefone") ?: ""
                        )
                        clientes.add(cliente) // Atualiza a lista de clientes
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                }
            }

            // Lista de clientes usando LazyColumn para rolagem
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(clientes) { cliente ->
                    // Cada cliente é exibido em uma linha com nome e telefone
                    Row(Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(0.5f)) {
                            Text(text = cliente.nome)
                        }
                        Column(modifier = Modifier.weight(0.5f)) {
                            Text(text = cliente.telefone)
                        }
                    }
                }
            }
        }
    }
}

// Data class representando cada cliente com campos para nome e telefone
data class Cliente(
    val nome: String = "",
    val telefone: String = ""
)

