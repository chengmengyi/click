package com.demo.click

import android.content.Intent
import android.net.Uri
import com.demo.click.app.LocalConfig
import com.demo.click.helper.toast
import kotlinx.android.synthetic.main.layout_set.*

class SetPage:FatherPage(R.layout.layout_set) {
    override fun init() {
        iv_back.setOnClickListener { finish() }
        ll_contact.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data= Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL,LocalConfig.EMAIL)
                startActivity(intent)
            }catch (e:Exception){
                toast("Contact us by email：${LocalConfig.EMAIL}")
            }
        }

        ll_privacy.setOnClickListener {
            startActivity(Intent(this,AgreementPage::class.java))
        }
    }
}