package com.demo.click.app

object LocalConfig {
    const val AGREEMENT=""
    const val EMAIL=""

    const val LOCAL_CITY="""{
   "click_city":[
      "Tokyo",
      "Singapore"
   ]
}"""


    const val SERVER="""{
   "click_server":[
      {
         "click_pwd":"123456",
         "click_method":"chacha20-ietf-poly1305",
         "click_port":100,
         "click_country":"Canada",
         "click_city":"Vancouver",
         "click_host":"100.223.52.0"
      },
      {
         "click_pwd":"123456",
         "click_method":"chacha20-ietf-poly1305",
         "click_port":100,
         "click_country":"Singapore",
         "click_city":"Singapore",
         "click_host":"100.223.52.78"
      }
   ]
}"""

    const val LOCAL_AD="""{
    "click_open": [
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/1033173712x",
            "click_type": "chaping",
            "click_sort": 1
        },
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/3419835294AA",
            "click_type": "kaiping",
            "click_sort": 2
        },
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/3419835294",
            "click_type": "kaiping",
            "click_sort": 3
        }
    ],
    "click_home": [
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/2247696110",
            "click_type": "yuansheng",
            "click_sort": 2
        },
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/2247696110",
            "click_type": "yuansheng",
            "click_sort": 1
        },
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/2247696110AA",
            "click_type": "yuansheng",
            "click_sort": 3
        }
    ],
    "click_result": [
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/2247696110",
            "click_type": "yuansheng",
            "click_sort": 2
        },
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/2247696110",
            "click_type": "yuansheng",
            "click_sort": 1
        },
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/2247696110AA",
            "click_type": "yuansheng",
            "click_sort": 3
        }
    ],
    "click_connect": [
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/8691691433x",
            "click_type": "chaping",
            "click_sort": 2
        },
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/1033173712AA",
            "click_type": "chaping",
            "click_sort": 1
        },
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/1033173712",
            "click_type": "chaping",
            "click_sort": 3
        }
    ],
    "click_back": [
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/1033173712",
            "click_type": "chaping",
            "click_sort": 2
        },
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/8691691433aa",
            "click_type": "chaping",
            "click_sort": 1
        },
        {
            "click_source": "admob",
            "click_id": "ca-app-pub-3940256099942544/1033173712",
            "click_type": "chaping",
            "click_sort": 3
        }
    ]
}"""


}