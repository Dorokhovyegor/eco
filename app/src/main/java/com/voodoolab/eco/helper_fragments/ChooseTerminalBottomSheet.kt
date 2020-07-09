package com.voodoolab.eco.helper_fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.elyeproj.loaderviewlibrary.LoaderImageView
import com.elyeproj.loaderviewlibrary.LoaderTextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.voodoolab.eco.R
import com.voodoolab.eco.utils.fadeInAnimation
import com.voodoolab.eco.utils.fadeOutAnimation
import kotlinx.android.synthetic.main.terminal_item.view.*

class ChooseTerminalBottomSheet(
    val systemID: String,
    val seats: Int,
    val address: String
) : BottomSheetDialogFragment() {

    private var fakeTitle: LoaderTextView? = null
    private var fakeSubtitle: LoaderTextView? = null
    private var fakeList: LoaderImageView? = null

    private var titleTextView: TextView? = null
    private var subtitleTextView: TextView? = null
    private var listTerminal: RecyclerView? = null

    var chooseTerminalListener: ChooseTerminalListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.choose_terminal_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (parentFragment is ChooseTerminalListener) {
            chooseTerminalListener = parentFragment as ChooseTerminalListener
        }
        initViews(view)
        titleTextView?.text = address
        subtitleTextView?.text = "Выберите терминал"
        listTerminal?.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        listTerminal?.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.terminal_item, parent, false)
                return FakeViewHolder(itemView)
            }

            override fun getItemCount(): Int {
               return seats
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                if (holder is FakeViewHolder) {
                    holder.textView.text = (position + 1).toString()
                }
            }

            inner class FakeViewHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {
                val textView = itemView.terminal_id
               init {
                   itemView.setOnClickListener(this)
               }

                override fun onClick(v: View?) {
                    context?.let {
                        AlertDialog.Builder(it)
                            .setTitle("Подтверждение")
                            .setMessage("Вы уверены, что хотите начать мойку по адресу ${address}, терминал №${adapterPosition + 1}?")
                            .setPositiveButton("Да") { w, a ->
                                chooseTerminalListener?.onTerminalClick("${systemID}-${adapterPosition}")
                                dismiss()
                            }
                            .setNegativeButton("Нет") { w, a ->
                                dismiss()
                            }
                            .show()
                    }
                }
            }
        }

        Handler().postDelayed({
            fakeTitle?.fadeOutAnimation()
            fakeList?.fadeOutAnimation()
            titleTextView?.fadeInAnimation()
            subtitleTextView?.fadeInAnimation()
            listTerminal?.fadeInAnimation()
        }, 900)
    }

    private fun initViews(view: View) {
        fakeTitle = view.findViewById(R.id.fake_title)
        fakeSubtitle = view.findViewById(R.id.fake_subtitle)
        fakeList = view.findViewById(R.id.fake_content)

        titleTextView = view.findViewById(R.id.title)
        subtitleTextView = view.findViewById(R.id.fake_subtitle)
        listTerminal = view.findViewById(R.id.terminals)
    }

    interface ChooseTerminalListener {
        fun onTerminalClick(code: String)
    }

}







