package ru.teamdroid.recipecraft.ui.navigation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_favorites.toolbar
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.image
import ru.teamdroid.recipecraft.AppModule
import ru.teamdroid.recipecraft.R
import ru.teamdroid.recipecraft.ui.base.BaseFragment
import ru.teamdroid.recipecraft.ui.base.CircleTransform
import ru.teamdroid.recipecraft.ui.base.Constants
import ru.teamdroid.recipecraft.ui.base.Screens
import ru.teamdroid.recipecraft.ui.navigation.components.DaggerProfileComponent
import ru.teamdroid.recipecraft.ui.navigation.contracts.ProfileContract
import ru.teamdroid.recipecraft.ui.navigation.modules.ProfilePresenterModule
import ru.teamdroid.recipecraft.ui.navigation.presenters.ProfilePresenter
import javax.inject.Inject

class ProfileFragment : BaseFragment(), ProfileContract.View {

    override val contentResId = R.layout.fragment_profile

    @Inject
    internal lateinit var presenter: ProfilePresenter

    private val clickListener = View.OnClickListener { view ->
        when (view.tag) {
            Screens.SETTINGS -> replaceScreen(SettingsFragment.newInstance())
            Screens.ABOUT -> replaceScreen(AboutFragment.newInstance())
            Screens.FAVORITE -> replaceScreen(FavoritesFragment.newInstance())
            Screens.FEEDBACK -> replaceScreen(FeedbackFragment.newInstance())
            Constants.SIGN_IN -> signIn()
            Constants.SIGN_OUT -> logout()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePresenter()
    }

    private fun initializePresenter() {
        DaggerProfileComponent.builder()
                .profilePresenterModule(ProfilePresenterModule(this))
                .appModule(AppModule(baseActivity.application))
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(toolbar, false, getString(R.string.fragment_profile_title))

        presenter.onAttachView()

        listOf(settingsTextView, aboutTextView, favoritesTextView,
                feedbackTextView, signInTextView, logoutTextView).forEach {
            it.setOnClickListener(clickListener)
        }
    }

    override fun showUserSignIn(displayName: String?, photoUrl: Uri?) {
        usernameTextView.text = displayName
        Picasso.with(context)
                .load(photoUrl)
                .placeholder(R.drawable.ic_placeholder)
                .transform(CircleTransform())
                .into(profileImageView)
        signInTextView.visibility = View.GONE
        logoutTextView.visibility = View.VISIBLE
    }

    override fun showUserSignOut() {
        usernameTextView.text = getString(R.string.not_authorization_client_text)
        profileImageView.image = ContextCompat.getDrawable(context, R.drawable.ic_placeholder)
        signInTextView.visibility = View.VISIBLE
        logoutTextView.visibility = View.GONE
    }

    private fun replaceScreen(fragment: Fragment) {
        baseActivity.replaceFragment(fragment, NavigationFragment.TAG)
    }

    private fun logout() {
        presenter.signOut()
    }

    private fun signIn() {
        presenter.signIn()
    }

    override fun signInWithGoogle(signInIntent: Intent) {
        startActivityForResult(signInIntent, Constants.REQUEST_CODE_SIGN_IN)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_SIGN_IN) {
            try {
                presenter.firebaseAuthWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java))
            } catch (e: ApiException) {
                Toast.makeText(context, getString(R.string.error_auth_text), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TAG = "ProfileFragment"
        fun newInstance() = ProfileFragment()
    }
}