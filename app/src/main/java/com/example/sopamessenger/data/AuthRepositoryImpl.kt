package com.example.sopamessenger.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.sopamessenger.data.Constant.WebServerClient
import com.example.sopamessenger.util.Resource
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message))
        }
    }

    override fun registerUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message))
        }
    }


    override fun googleSignIn(context: Context): Flow<Resource<AuthResult>> {
        val credentialManager = CredentialManager.create(context)

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WebServerClient)
            .setNonce(hashedNonce)
            .build()

        val credentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return flow {
            emit(Resource.Loading())

            try {
                // Запитуємо облікові дані через Credential Manager
                val result = credentialManager.getCredential(
                    context = context,
                    request = credentialRequest
                )

                // Отримуємо токен з результату
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)

                val googleIdToken = googleIdTokenCredential.idToken

                // Створюємо AuthCredential для Firebase
                val credential = GoogleAuthProvider.getCredential(googleIdToken, hashedNonce)

                // Виконуємо вхід через Firebase
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                emit(Resource.Success(authResult))

            } catch (e: GetCredentialException) {
                emit(Resource.Error(e.message ?: "Failed to get Google credential"))
            } catch (e: GoogleIdTokenParsingException) {
                emit(Resource.Error(e.message ?: "Failed to parse Google ID Token"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error: ${e.localizedMessage}"))
            }
        }.catch { e ->
            emit(Resource.Error(e.message ?: "Unknown error in flow: ${e.localizedMessage}"))
        }
    }
}