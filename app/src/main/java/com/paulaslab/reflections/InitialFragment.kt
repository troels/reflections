package com.paulaslab.reflections

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * A simple [Fragment] subclass.
 * Use the [InitialFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InitialFragment : Fragment() {
    var recyclerView: RecyclerView? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentManager = this.parentFragmentManager
        val app = activity?.application as ReflectionsApp
        val id = app.journalStore!!.newEntryId()
        val file = app.journalStore!!.getEntryFile(id)

        app.journalStore!!.getEntryFile(0)
        val handler = Handler(context!!.mainLooper)
        val journallingFragment = JournallingFragment.newInstance(
            this,
            file,
            id,
            {
                val pos = app.journalStore!!.listGoodEntries()


                Log.i("Recycler", "Add item: ${pos[pos.size - 1]}")
                handler.post(Runnable {
                    val adapter = getView()?.findViewById<RecyclerView>(R.id.diary_entry_container)?.adapter
                    Log.i("HELLO", "Here and dataset changed: ${adapter}")
                    if (adapter != null) {
                        (adapter as DiaryEntryRow).updateIt()
                    }
                })
            }
        )

        val view = inflater.inflate(R.layout.fragment_initial, container, false)

        view.findViewById<Button>(R.id.start_journalling_button)?.setOnTouchListener(
            View.OnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_UP -> {
                        val ft: FragmentTransaction = fragmentManager.beginTransaction()
                        ft.replace(R.id.flContainer, journallingFragment)
                        ft.commit()
                    }
                }
                false
            })

        // set up the RecyclerView
        recyclerView = view.findViewById<RecyclerView>(R.id.diary_entry_container)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        val adapter = DiaryEntryRow(context!!, app.journalStore!!)
        recyclerView?.adapter = adapter

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = InitialFragment()
    }
}