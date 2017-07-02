package com.togglecorp.paiso.containers


class NotificationListFragment : android.support.v4.app.Fragment() {

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {
        val view = inflater!!.inflate(com.togglecorp.paiso.R.layout.fragment_notification_list, container, false)

        return view
    }

}
