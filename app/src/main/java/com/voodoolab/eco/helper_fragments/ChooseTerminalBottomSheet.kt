package com.voodoolab.eco.helper_fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

class ChooseTerminalBottomSheet : BottomSheetDialogFragment() {

    private var fakeTitle: LoaderTextView? = null
    private var fakeSubtitle: LoaderTextView? = null
    private var fakeList: LoaderImageView? = null

    private var titleTextView: TextView? = null
    private var subtitleTextView: TextView? = null
    private var listTerminal: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.choose_terminal_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)

        titleTextView?.text = "Название улицы"
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
               return 10
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                if (holder is FakeViewHolder) {
                    holder.textView.text = position.toString()
                }
            }

            inner class FakeViewHolder(item: View): RecyclerView.ViewHolder(item) {
                val textView = itemView.terminal_id
            }

        }

        Handler().postDelayed({
            fakeTitle?.fadeOutAnimation()
            fakeSubtitle?.fadeOutAnimation()
            fakeList?.fadeOutAnimation()

            titleTextView?.fadeInAnimation()
            subtitleTextView?.fadeInAnimation()
            listTerminal?.fadeInAnimation()
        }, 900)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun initViews(view: View) {
        fakeTitle = view.findViewById(R.id.fake_title)
        fakeSubtitle = view.findViewById(R.id.fake_subtitle)
        fakeList = view.findViewById(R.id.fake_content)

        titleTextView = view.findViewById(R.id.title)
        subtitleTextView = view.findViewById(R.id.fake_subtitle)
        listTerminal = view.findViewById(R.id.terminals)
    }
}







