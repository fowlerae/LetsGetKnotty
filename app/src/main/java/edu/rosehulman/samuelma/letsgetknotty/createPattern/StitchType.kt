package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.graphics.Color

enum class StitchType {
    KNIT {
        fun getDrawable(): String = ""
    },
    PURL{
        fun getDrawable(color: Color): String{
            return "R.drawable.ic_stitch_purl_black"
//            if ((color.red()*0.299 + color.green()*0.587 + color.blue()*0.114) > 186){
//               return "R.drawable.ic_stitch_purl_black"
//            }else{
//                return "R.drawable.ic_stitch_purl_white"
//            }
        }
    },
    YARNOVER{
        fun getDrawable(color: Color): String{
            return "R.drawable.ic_stitch_yarnover_black"
        }
    };
}