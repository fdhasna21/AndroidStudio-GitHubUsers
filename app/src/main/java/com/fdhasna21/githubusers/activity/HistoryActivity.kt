package com.fdhasna21.githubusers.activity

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fdhasna21.githubusers.R
import com.fdhasna21.githubusers.adapter.UserItemSwipeCallback
import com.fdhasna21.githubusers.adapter.UserRowAdapter
import com.fdhasna21.githubusers.databinding.ActivityHistoryBinding
import com.fdhasna21.githubusers.model.response.UserResponse
import com.fdhasna21.githubusers.utility.DialogUtils
import com.fdhasna21.githubusers.utility.type.ErrorType
import com.fdhasna21.githubusers.viewmodel.HistoryViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Fernanda Hasna on 26/09/2024.
 * Updated by Fernanda Hasna on 27/09/2024.
 */

class HistoryActivity : BaseActivity<ActivityHistoryBinding, HistoryViewModel>(
    ActivityHistoryBinding::inflate,
    HistoryViewModel::class.java
), SwipeRefreshLayout.OnRefreshListener {

    override val viewModel: HistoryViewModel by viewModel()

    private lateinit var rowAdapter: UserRowAdapter
    private lateinit var layoutManager : LinearLayoutManager

    override fun setupData() {
        viewModel.getAllHistoriesFromRepository()
    }

    override fun setupUIWhenConfigChange() {
        setupToolbar()
        setupRecyclerView()
        super.setupUIWhenConfigChange()
    }

    override fun setupUIWithoutConfigChange() {
        viewModel.isLoading.observe(this){
            when(it){
                true -> {
                    binding.historyResponse.progressCircular.visibility = View.VISIBLE
                }
                false -> {
                    binding.historyResponse.progressCircular.visibility = View.INVISIBLE
                    binding.refreshRecyclerView.isRefreshing = false
                }
            }
        }
        viewModel.allHistories.observe(this){
            if (it == null || it.isEmpty()) {
//                if(viewModel.isLoading.value == false){
                    val error: ArrayList<Int> = ErrorType.DATA_EMPTY.setError(this)
                    binding.historyResponse.layoutError.visibility = View.VISIBLE
                    binding.historyResponse.errorImage.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this,
                            error[0]
                        )
                    )
                    binding.historyResponse.errorMessage.text = getString(error[1])
                    binding.recyclerView.visibility = View.INVISIBLE
//                }
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                rowAdapter.updateData(it)
            }
        }
    }

    private fun setupToolbar(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = getString(R.string.history)
    }

    private fun setupRecyclerView(){
        binding.apply {
            refreshRecyclerView.setOnRefreshListener(this@HistoryActivity)
            rowAdapter = UserRowAdapter(this@HistoryActivity, arrayListOf())
            layoutManager = LinearLayoutManager(this@HistoryActivity)

            recyclerView.apply {
                layoutManager = this@HistoryActivity.layoutManager
                adapter = rowAdapter
                addItemDecoration(object : DividerItemDecoration(this@HistoryActivity, VERTICAL) {})
                setHasFixedSize(true)
            }

            val itemTouchHelper = ItemTouchHelper(object : UserItemSwipeCallback(rowAdapter){
                override fun onItemSwipeListener(user: UserResponse, position: Int) {
                    viewModel.deleteHistoryFromRepository(user)
                    Snackbar.make(recyclerView, String.format(getString(R.string.success_deleted), user.username), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo){
                            rowAdapter.restoreItem(user, position)
                            viewModel.restoreHistoryToRepository(user)
                        }.show()
                }
            })
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history_delete -> {
                DialogUtils.showConfirmationDialog(this, getString(R.string.warning_delete_history)){
                    _, _ -> viewModel.deleteAllHistoriesFromRepository()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        setupData()
    }

    override fun onRefresh() {
        setupData()
    }
}