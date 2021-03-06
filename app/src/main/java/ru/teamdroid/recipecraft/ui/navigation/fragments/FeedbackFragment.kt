package ru.teamdroid.recipecraft.ui.navigation.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_feedback.*
import ru.teamdroid.recipecraft.R
import ru.teamdroid.recipecraft.ui.base.BaseMoxyFragment
import ru.teamdroid.recipecraft.ui.navigation.components.DaggerFeedbackComponent
import ru.teamdroid.recipecraft.ui.navigation.dialogs.ReportDialog
import ru.teamdroid.recipecraft.ui.navigation.presenters.FeedbackPresenter
import ru.teamdroid.recipecraft.ui.navigation.views.FeedbackView
import javax.inject.Inject

class FeedbackFragment : BaseMoxyFragment(), FeedbackView {

    @Inject
    @InjectPresenter
    lateinit var presenter: FeedbackPresenter

    @ProvidePresenter
    fun providePresenter(): FeedbackPresenter {
        DaggerFeedbackComponent.builder()
                .recipeRepositoryComponent(baseActivity.recipeRepositoryComponent)
                .build()
                .inject(this)
        return presenter
    }

    override val contentResId = R.layout.fragment_feedback

    private var reportDialog: ReportDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(toolbar, true, getString(R.string.fragment_report_title))
        submitFeedbackButton.setOnClickListener {
            if (nameEditText.text.isNotBlank() && emailEditText.text.isNotBlank() && feedbackEditText.text.isNotBlank()) {
                progressBar.visibility = View.VISIBLE
                sendReport(nameEditText.text.toString(), emailEditText.text.toString(), feedbackEditText.text.toString())
            } else {
                Toast.makeText(context, R.string.error_input_text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendReport(name: String, email: String, text: String) {
        reportDialog = ReportDialog()
        reportDialog?.show(childFragmentManager, TAG)
        presenter.sendReportMessage(name, email, text)
    }

    override fun onSuccess() {
        progressBar.visibility = View.GONE
        Toast.makeText(context, R.string.thanks_feedback, Toast.LENGTH_SHORT).show()
        reportDialog?.dismiss()
        reportDialog = null
    }

    override fun onFailure() {
        progressBar.visibility = View.GONE
        Toast.makeText(context, R.string.error_feedback, Toast.LENGTH_SHORT).show()
        reportDialog?.dismiss()
        reportDialog = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reportDialog?.dismiss()
        reportDialog = null
    }

    companion object {
        const val TAG = "RecipesFragment"
        fun newInstance() = FeedbackFragment()
    }
}