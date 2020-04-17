package com.xlbp.afridgetoofar;

import androidx.appcompat.app.AppCompatActivity;

public abstract class DeliveryAppBaseActivity extends AppCompatActivity
{
    public abstract void onDocumentComplete();

    public abstract void onAddressNotFound();
}
