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

import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc.ICalculatedCadenceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc.IRawCadenceDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.ICumulativeOperatingTimeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IManufacturerAndSerialReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IVersionAndModelReceiver;
import com.example.savinghearts.R;

import java.math.BigDecimal;
import java.util.EnumSet;

/**
 * Connects to Bike Cadence Plugin and display all the event data.
 */
public class Activity_BikeCadenceSampler extends Activity
{
    AntPlusBikeCadencePcc bcPcc = null;
    AntPlusBikeSpeedDistancePcc bsPcc = null;

    TextView tv_status;

    TextView tv_estTimestamp;

    TextView tv_calculatedCadence;
    TextView tv_cumulativeRevolutions;
    TextView tv_timestampOfLastEvent;

    TextView tv_isSpdAndCadCombo;
    TextView tv_calculatedSpeed;

    TextView tv_cumulativeOperatingTime;

    TextView tv_manufacturerID;
    TextView tv_serialNumber;

    TextView tv_hardwareVersion;
    TextView tv_softwareVersion;
    TextView tv_modelNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_cadence);

        tv_status = (TextView)findViewById(R.id.textView_Status);

        tv_estTimestamp = (TextView)findViewById(R.id.textView_EstTimestamp);

        tv_calculatedCadence = (TextView)findViewById(R.id.textView_CaluclatedCadence);
        tv_cumulativeRevolutions = (TextView)findViewById(R.id.textView_CumulativeRevolutions);
        tv_timestampOfLastEvent = (TextView)findViewById(R.id.textView_TimestampOfLastEvent);

        tv_isSpdAndCadCombo = (TextView)findViewById(R.id.textView_IsCombinedSensor);
        tv_calculatedSpeed = (TextView)findViewById(R.id.textView_CalculatedSpeed);

        tv_cumulativeOperatingTime = (TextView)findViewById(R.id.textView_CumulativeOperatingTime);

        tv_manufacturerID = (TextView)findViewById(R.id.textView_ManufacturerID);
        tv_serialNumber = (TextView)findViewById(R.id.textView_SerialNumber);

        tv_hardwareVersion = (TextView)findViewById(R.id.textView_HardwareVersion);
        tv_softwareVersion = (TextView)findViewById(R.id.textView_SoftwareVersion);
        tv_modelNumber = (TextView)findViewById(R.id.textView_ModelNumber);

        resetPcc();
    }

    /**
     * Resets the PCC connection to request access again and clears any existing display data.
     */    
    private void resetPcc()
    {
        //Release the old access if it exists
        if(bcPcc != null)
        {
            bcPcc.releaseAccess();
            bcPcc = null;
        }
        if(bsPcc != null)
        {
            bsPcc.releaseAccess();
            bsPcc = null;
        }


        //Reset the text display
        tv_status.setText("Connecting...");

        tv_estTimestamp.setText("---");

        tv_calculatedCadence.setText("---");
        tv_cumulativeRevolutions.setText("---");
        tv_timestampOfLastEvent.setText("---");

        tv_isSpdAndCadCombo.setText("---");
        tv_calculatedSpeed.setText("---");

        tv_cumulativeOperatingTime.setText("---");

        tv_manufacturerID.setText("---");
        tv_serialNumber.setText("---");

        tv_hardwareVersion.setText("---");
        tv_softwareVersion.setText("---");
        tv_modelNumber.setText("---");

        //Make the access request
        AntPlusBikeCadencePcc.requestAccess(this, this, 
            //AntPlusBikeCadencePcc.requestAccess(this, 0, 0, false,    //Asynchronous request mode
            new IPluginAccessResultReceiver<AntPlusBikeCadencePcc>()
            {

            //Handle the result, connecting to events on success or reporting failure to user.
            @Override
            public void onResultReceived(AntPlusBikeCadencePcc result,
                RequestAccessResult resultCode, DeviceState initialDeviceState)
            {
                switch(resultCode)
                {
                    case SUCCESS:
                        bcPcc = result;
                        tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                        subscribeToEvents();
                        break;
                    case CHANNEL_NOT_AVAILABLE:
                        Toast.makeText(Activity_BikeCadenceSampler.this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case OTHER_FAILURE:
                        Toast.makeText(Activity_BikeCadenceSampler.this, "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case DEPENDENCY_NOT_INSTALLED:
                        tv_status.setText("Error. Do Menu->Reset.");
                        AlertDialog.Builder adlgBldr = new AlertDialog.Builder(Activity_BikeCadenceSampler.this);
                        adlgBldr.setTitle("Missing Dependency");
                        adlgBldr.setMessage("The required service\n\"" + AntPlusBikeCadencePcc.getMissingDependencyName() + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                        adlgBldr.setCancelable(true);
                        adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent startStore = null;
                                startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusBikeCadencePcc.getMissingDependencyPackageName()));
                                startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                Activity_BikeCadenceSampler.this.startActivity(startStore);
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
                        Toast.makeText(Activity_BikeCadenceSampler.this, "Failed: UNRECOGNIZED. Upgrade Required?", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    default:
                        Toast.makeText(Activity_BikeCadenceSampler.this, "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                } 
            }

            /**
             * Subscribe to all the heart rate events, connecting them to display their data.
             */
            private void subscribeToEvents()
            {
                bcPcc.subscribeCalculatedCadenceEvent(new ICalculatedCadenceReceiver()
                {

                    @Override
                    public void onNewCalculatedCadence(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final BigDecimal calculatedCadence)
                    {
                        runOnUiThread(new Runnable()
                        {                                            
                            @Override
                            public void run()
                            {
                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                tv_calculatedCadence.setText(String.valueOf(calculatedCadence));
                            }
                        });

                    }
                });

                bcPcc.subscribeRawCadenceDataEvent(new IRawCadenceDataReceiver()
                {

                    @Override
                    public void onNewRawCadenceData(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final BigDecimal timestampOfLastEvent,
                        final long cumulativeRevolutions)
                    {
                        runOnUiThread(new Runnable()
                        {                                            
                            @Override
                            public void run()
                            {
                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                tv_timestampOfLastEvent.setText(String.valueOf(timestampOfLastEvent));

                                tv_cumulativeRevolutions.setText(String.valueOf(cumulativeRevolutions));
                            }
                        });
                    }
                });

                if(bcPcc.isSpeedAndCadenceCombinedSensor())
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {                                            
                            tv_isSpdAndCadCombo.setText("Yes");
                            tv_cumulativeOperatingTime.setText("N/A");
                            tv_manufacturerID.setText("N/A");
                            tv_serialNumber.setText("N/A");
                            tv_hardwareVersion.setText("N/A");
                            tv_softwareVersion.setText("N/A");
                            tv_modelNumber.setText("N/A");

                            tv_calculatedSpeed.setText("...");
                        }
                    });
            AntPlusBikeSpeedDistancePcc.requestAccess(Activity_BikeCadenceSampler.this, bcPcc.getAntDeviceNumber(), 0, true, 
                    new IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc>()
                    {
                        //Handle the result, connecting to events on success or reporting failure to user.
                        @Override
                        public void onResultReceived(AntPlusBikeSpeedDistancePcc result, RequestAccessResult resultCode,
                                DeviceState initialDeviceStateCode)
                        {
                            switch(resultCode)
                            {
                                case SUCCESS:
                                    bsPcc = result;
                                    bsPcc.subscribeCalculatedSpeedEvent(new CalculatedSpeedReceiver(new BigDecimal(2.095))
                                            {
                                                @Override
                                                public void onNewCalculatedSpeed(long estTimestamp,
                                                        EnumSet<EventFlag> eventFlags,
                                                        final BigDecimal calculatedSpeed)
                                                {
                                                    runOnUiThread(new Runnable()
                                                    {                                            
                                                        @Override
                                                        public void run()
                                                        {
                                                            tv_calculatedSpeed.setText(String.valueOf(calculatedSpeed));
                                                        }
                                                    });
                                                }
                                            });
                                    break;
                                case CHANNEL_NOT_AVAILABLE:
                                    tv_calculatedSpeed.setText("CHANNEL NOT AVAILABLE");
                                    break;
                                case OTHER_FAILURE:
                                    tv_calculatedSpeed.setText("OTHER FAILURE");
                                    break;
                                case DEPENDENCY_NOT_INSTALLED:
                                    tv_calculatedSpeed.setText("DEPENDENCY NOT INSTALLED");
                                    break;
                                default:
                                    tv_calculatedSpeed.setText("UNRECOGNIZED ERROR: " + resultCode);
                                    break;
                            } 
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
                                                    if(newDeviceState != DeviceState.TRACKING)
                                                        tv_calculatedSpeed.setText(newDeviceState.toString());
                                                    if(newDeviceState == DeviceState.DEAD)
                                                        bsPcc = null;
                                                }
                                            });
                                    
                                    
                                }
                            } );
                }
                else
                {
                    //Subscribe to the events available in the pure cadence profile
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_isSpdAndCadCombo.setText("No");
                            tv_calculatedSpeed.setText("N/A");
                        }
                    });

                    bcPcc.subscribeCumulativeOperatingTimeEvent(new ICumulativeOperatingTimeReceiver()
                    {
                        @Override
                        public void onNewCumulativeOperatingTime(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final long cumulativeOperatingTime)
                        {
                            runOnUiThread(new Runnable()
                            {                                            
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                    tv_cumulativeOperatingTime.setText(String.valueOf(cumulativeOperatingTime));
                                }
                            });
                        }
                    });

                    bcPcc.subscribeManufacturerAndSerialEvent(new IManufacturerAndSerialReceiver()
                    {
                        @Override
                        public void onNewManufacturerAndSerial(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int manufacturerID,
                            final int serialNumber)
                        {
                            runOnUiThread(new Runnable()
                            {                                            
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                    tv_manufacturerID.setText(String.valueOf(manufacturerID));
                                    tv_serialNumber.setText(String.valueOf(serialNumber));
                                }
                            });
                        }
                    });

                    bcPcc.subscribeVersionAndModelEvent(new IVersionAndModelReceiver()
                    {
                        @Override
                        public void onNewVersionAndModel(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int hardwareVersion,
                            final int softwareVersion, final int modelNumber)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                    
                                    tv_hardwareVersion.setText(String.valueOf(hardwareVersion));
                                    tv_softwareVersion.setText(String.valueOf(softwareVersion));
                                    tv_modelNumber.setText(String.valueOf(modelNumber));
                                }
                            });
                            AntPlusBikeSpeedDistancePcc.requestAccess(Activity_BikeCadenceSampler.this, bcPcc.getAntDeviceNumber(), 0, true, 
                                new IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc>()
                                {
                                //Handle the result, connecting to events on success or reporting failure to user.
                                @Override
                                public void onResultReceived(AntPlusBikeSpeedDistancePcc result,
                                    RequestAccessResult resultCode, DeviceState initialDeviceState)
                                {
                                    switch(resultCode)
                                    {
                                        case SUCCESS:
                                            bsPcc = result;
                                            bsPcc.subscribeCalculatedSpeedEvent(new CalculatedSpeedReceiver(new BigDecimal(2.095))
                                            {                                                        
                                                @Override
                                                public void onNewCalculatedSpeed(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final BigDecimal calculatedSpeed)
                                                {
                                                    runOnUiThread(new Runnable()
                                                    {                                            
                                                        @Override
                                                        public void run()
                                                        {
                                                            tv_calculatedSpeed.setText(String.valueOf(calculatedSpeed));
                                                        }
                                                    });

                                                }
                                            });
                                            break;
                                        case CHANNEL_NOT_AVAILABLE:
                                            tv_calculatedSpeed.setText("CHANNEL NOT AVAILABLE");
                                            break;
                                        case OTHER_FAILURE:
                                            tv_calculatedSpeed.setText("OTHER FAILURE");
                                            break;
                                        case DEPENDENCY_NOT_INSTALLED:
                                            tv_calculatedSpeed.setText("DEPENDENCY NOT INSTALLED");
                                            break;
                                        case UNRECOGNIZED:
                                            //TODO This flag indicates that an unrecognized value was sent by the service, an upgrade of your PCC may be required to handle this new value.
                                            Toast.makeText(Activity_BikeCadenceSampler.this, "Failed: UNRECOGNIZED. Upgrade Required?", Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            tv_calculatedSpeed.setText("UNRECOGNIZED ERROR: " + resultCode);
                                            break;
                                    } 
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
                                                if(newDeviceState != DeviceState.TRACKING)
                                                    tv_calculatedSpeed.setText(newDeviceState.toString());
                                                if(newDeviceState == DeviceState.DEAD)
                                                    bsPcc = null;
                                            }
                                        });
                                    }
                                } );
                        }
                    });
                }
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
                            tv_status.setText(bcPcc.getDeviceName() + ": " + newDeviceState.toString());
                            if(newDeviceState == DeviceState.DEAD)
                                bcPcc = null;
                        }
                    });
                }
            } );
    }

    @Override
    protected void onDestroy()
    {
        if(bcPcc != null)
        {
            bcPcc.releaseAccess();
            bcPcc = null;
        }
        if(bsPcc != null)
        {
            bsPcc.releaseAccess();
            bsPcc = null;
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
