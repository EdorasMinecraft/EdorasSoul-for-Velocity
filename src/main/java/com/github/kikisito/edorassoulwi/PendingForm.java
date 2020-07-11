package com.github.kikisito.edorassoulwi;

import com.google.gson.JsonArray;

public class PendingForm {
    private boolean isPendingForms;
    private JsonArray pendingForms;

    public PendingForm(boolean isPendingForms, JsonArray pendingForms){
        this.isPendingForms = isPendingForms;
        this.pendingForms = pendingForms;
    }

    public boolean isPendingForms(){
        return isPendingForms;
    }

    public JsonArray getJsonArray(){
        return pendingForms;
    }

}
