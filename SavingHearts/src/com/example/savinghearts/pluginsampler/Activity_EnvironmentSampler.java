/*
This software is subject to the license described in the License.txt file 
included with this software distribution. You may not use this file except in compliance 
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
 */

package com.example.savinghearts.pluginsampler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.plugins.antplus.pcc.AntPlusEnvironmentPcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusEnvironmentPcc.ITemperatureDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IManufacturerIdentificationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IProductInformationReceiver;
import com.example.savinghearts.R;

import java.math.BigDecimal;
import java.util.EnumSet;

/**
 * Connects to Environment Plugin and display all the event data.
 */
public class Activity_EnvironmentSampler extends Activity
{
    AntPlusEnvironmentPcc envPcc = null;

    TextView tv_status;

    TextView tv_estTimestamp;

    TextView tv_currentTemperature;
    TextView tv_eventCount;
    TextView tv_lowLast24Hours;    
    TextView tv_highLast24Hours;

    TextView tv_hardwareRevision;
    TextView tv_manufacturerID;
    TextView tv_modelNumber;

    TextView tv_softwareRevision;
    TextView tv_serialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);

        tv_status = (TextView)findViewById(R.id.textView_Status);

        tv_estTimestamp = (TextView)findViewById(R.id.textView_EstTimestamp);

        tv_currentTemperature = (TextView)findViewById(R.id.textView_CurrentTemperature);
        tv_eventCount = (TextView)findViewById(R.id.textView_EventCount);
        tv_lowLast24Hours = (TextView)findViewById(R.id.textView_LowLast24Hours);   
        tv_highLast24Hours = (TextView)findViewById(R.id.textView_HighLast24Hours);

        tv_hardwareRevision = (TextView)findViewById(R.id.textView_HardwareRevision);
        tv_manufacturerID = (TextView)findViewById(R.id.textView_ManufacturerID);
        tv_modelNumber = (TextView)findViewById(R.id.textView_ModelNumber);

        tv_softwareRevision = (TextView)findViewById(R.id.textView_SoftwareRevision);
        tv_serialNumber = (TextView)findViewById(R.id.textView_SerialNumber);

        resetPcc();
    }

    /**
     * Resets the PCC connection to request access again and clears any existing display data.
     */ 
    private void resetPcc()
    {
        //Release the old access if it exists
        if(envPcc != null)
        {
            envPcc.releaseAccess();
            envPcc = null;
        }


        //Reset the text display
        tv_status.setText("Connecting...");

        tv_estTimestamp.setText("---");

        tv_currentTemperature.setText("---");
        tv_eventCount.setText("---");
        tv_lowLast24Hours.setText("---");    
        tv_highLast24Hours.setText("---");

        tv_hardwareRevision.setText("---");
        tv_manufacturerID.setText("---");
        tv_modelNumber.setText("---");

        tv_softwareRevision.setText("---");
        tv_serialNumber.setText("---");


        //Make the access request
        AntPlusEnvironmentPcc.requestAccess(this, this,
            new IPluginAccessResultReceiver<AntPlusEnvironmentPcc>()
            {         
            //Handle the result, connecting to events on success or reporting failure to user.
            @Override
            public void onResultReceived(AntPlusEnvironmentPcc result,
                RequestAccessResult resultCode, DeviceState initialDeviceState)
            {
                switch(resultCode)
                {
                    case SUCCESS:
                        envPcc = result;
                        tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                        subscribeToEvents();
                        break;
                    case CHANNEL_NOT_AVAILABLE:
                        Toast.makeText(Activity_EnvironmentSampler.this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case OTHER_FAILURE:
                        Toast.makeText(Activity_EnvironmentSampler.this, "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case DEPENDENCY_NOT_INSTALLED:
                        tv_status.setText("Error. Do Menu->Reset.");
                        AlertDialog.Builder adlgBldr = new AlertDialog.Builder(Activity_EnvironmentSampler.this);
                        adlgBldr.setTitle("Missing Dependency");
                        adlgBldr.setMessage("The required service\n\"" + AntPlusEnvironmentPcc.getMissingDependencyName() + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                        adlgBldr.setCancelable(true);
                        adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent startStore = null;
                                startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusEnvironmentPcc.getMissingDependencyPackageName()));
                                startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                Activity_EnvironmentSampler.this.startActivity(startStore);                                                
                            }
                        });
                        adlgBldr.setNegativeButton("Cancel", new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

                        final AlertDialog waitDialog = adlgBldr.create();
                        waitDialog.show();
                        break;
                    case USER_CANCELLED:
                        tv_status.setText("Cancelled. Do Menu->Reset.");
                        break;
                    case UNRECOGNIZED:
                        //TODO This flag indicates that an unrecognized value was sent by the service, an upgrade of your PCC may be required to handle this new value.
                        Toast.makeText(Activity_EnvironmentSampler.this, "Failed: UNRECOGNIZED. Upgrade Required?", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    default:
                        Toast.makeText(Activity_EnvironmentSampler.this, "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                } 
            }

            /**
             * Subscribe to all the heart rate events, connecting them to display their data.
             */
            private void subscribeToEvents()
            {
                envPcc.subscribeTemperatureDataEvent(new ITemperatureDataReceiver()
                {

                    @Override
                    public void onNewTemperatureData(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final BigDecimal currentTemperature,
                        final long eventCount, final BigDecimal lowLast24Hours, final BigDecimal highLast24Hours)
                    {
                        runOnUiThread(new Runnable()
                        {                                            
                            @Override
                            public void run()
                            {
                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                tv_currentTemperature.setText(String.valueOf(currentTemperature));
                                tv_eventCount.setText(String.valueOf(eventCount));
                                tv_lowLast24Hours.setText(String.valueOf(lowLast24Hours));
                                tv_highLast24Hours.setText(String.valueOf(highLast24Hours));
                            }
                        });                                
                    }
                });

                envPcc.subscribeManufacturerIdentificationEvent(new IManufacturerIdentificationReceiver()
                {

                    @Override
                    public void onNewManufacturerIdentification(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int hardwareRevision,
                        final int manufacturerID, final int modelNumber)
                    {
                        runOnUiThread(new Runnable()
                        {                                            
                            @Override
                            public void run()
                            {
                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                tv_hardwareRevision.setText(String.valueOf(hardwareRevision));
                                tv_manufacturerID.setText(String.valueOf(manufacturerID));
                                tv_modelNumber.setText(String.valueOf(modelNumber));
                            }
                        });
                    }
                });

                envPcc.subscribeProductInformationEvent(new IProductInformationReceiver()
                {

                    @Override
                    public void onNewProductInformation(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int softwareRevision,
                        final long serialNumber)
                    {
                        runOnUiThread(new Runnable()
                        {                                            
                            @Override
                            public void run()
                            {
                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                tv_softwareRevision.setText(String.valueOf(softwareRevision));
                                tv_serialNumber.setText(String.valueOf(serialNumber));
                            }
                        });
                    }
                });
            }
            }, 
            //Receives state changes and shows it on the status display line
            new IDeviceStateChangeReceiver()
            {
                @Override
                public void onDeviceStateChange(final DeviceState newDeviceState)
                {
                    runOnUiThread(new Runnable()
                    {                                            
                        @Override
                        public void run()
                        {
                            tv_status.setText(envPcc.getDeviceName() + ": " + newDeviceState);
                            if(newDeviceState == DeviceState.DEAD)
                                envPcc = null;
                        }
                    });
                }
            } );
    }

    @Override
    protected void onDestroy()
    {
        if(envPcc != null)
        {
            envPcc.releaseAccess();
            envPcc = null;
        }
        super.onDestroy();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_heart_rate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_reset:
                resetPcc();
                tv_status.setText("Resetting...");
                return true;
            default:
                return super.onOptionsItemSelected(item);                
        }
    }*/
}
