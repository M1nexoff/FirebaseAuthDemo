package uz.m1nex.testfirebaseauth

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider

object AppRepository {
    val isLogined: Boolean
        get() = pref.isLogin
    private val pref by lazy { Pref.getInstance() }
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    /*
    Login with Email & Password
    just add singing method to firebase
    */
    fun loginWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
                pref.isLogin = true
                pref.email = email
                pref.password = password
            }
            .addOnFailureListener {
                if (it.message?.contains("malformed") == true) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            pref.isLogin = true
                            pref.email = email
                            pref.password = password
                            onSuccess()
                        }
                        .addOnFailureListener { ex ->
                            onFailure(ex.message.toString())
                        }
                } else {
                    onFailure(it.message.toString())
                }
            }
    }

    /*
    Login with Google
    1. add signing method to firebase
    2. get SHA-1 by this https://stackoverflow.com/questions/27609442/how-to-get-the-sha-1-fingerprint-certificate-in-android-studio-for-debug-mode (if u can't see singing report enable experimental features>Configure all gradle task during sync and sync project)
    2. add SHA-1 to firebase app (u can do it to already created app too in firebase>project settings>general>your apps select one and add SHA-1)
    3. add or update google-services.json in app folder (not module!)
    4. rebuild project after adding google-services.json (or clean and build)
    then u will be able to see R.string.default_web_client_id
    */
    suspend fun loginWithGoogle(
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false) // show all accounts
                .build()

            val request = GetCredentialRequest.Builder()

                .addCredentialOption(googleIdOption)
                .build()

            // Launch the Google sign-in flow
            val result = credentialManager.getCredential(context, request)

            val credential = result.credential

            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                firebaseAuth.signInWithCredential(firebaseCredential)
                    .addOnSuccessListener {
                        pref.isLogin = true
                        pref.gmail = it.user?.email
                        pref.name = it.user?.displayName
                        pref.photoUrl = it.user?.photoUrl.toString()
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure(e.message ?: "Auth failed")
                    }
            } else {
                onFailure("Unsupported credential type: ${credential.type}")
            }
        } catch (e: Exception) {
            onFailure(e.message ?: "Unknown error")
        }
    }

    /*
    Login with GitHub
    1. open signing method in firebase and choose Github. It will ask github Client ID and Client secret and at bottom bottom will be link with handler (for github).
    2. open https://github.com/settings/applications/new and register new OAuth app. name(just name) url(i made repository with readme.md and type about this app) and authorization callback url (u need put handler link from firebase as i said).
    3. register app and generate Client Secret copy and paste id and secret to firebase signing method.
    */
    fun loginWithGithub(
        activity: Activity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val provider = OAuthProvider.newBuilder("github.com")

        provider.addCustomParameter("allow_signup", "false")

        // You can also add additional other parameters if needed
        provider.scopes = listOf("user:email")

        firebaseAuth
            .startActivityForSignInWithProvider(activity, provider.build())
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                pref.isLogin = true
                pref.github = user?.email
                pref.name = user?.displayName
                pref.photoUrl = user?.photoUrl.toString()
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message.toString())
            }
    }

    fun logout(onSuccess: () -> Unit) {
        firebaseAuth.signOut()
        pref.clear() // clear saved user data
        onSuccess()
    }

    fun deleteAccount(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            user.delete()
                .addOnSuccessListener {
                    pref.clear()
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure(e.message.toString())
                }
        } else {
            onFailure("No user logged in")
        }
    }
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

}
