package com.smn.cpfmaeklong;

/**
 * Created by Mink on 12/30/2015.
 */


        import android.app.Dialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Color;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Toast;

        import com.jjoe64.graphview.GraphView;
        import com.jjoe64.graphview.LegendRenderer;
        import com.jjoe64.graphview.helper.StaticLabelsFormatter;
        import com.jjoe64.graphview.series.DataPoint;
        import com.jjoe64.graphview.series.LineGraphSeries;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Date;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AeratorDailyGraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AeratorDailyGraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AeratorDailyGraphFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int MAX_AERATOR = 12;

    Date date = new Date();
    String mStrSelectedDay;

    private String mBaseUrl;
    private int mSelectedPond;
    private String mStrGraphGroup;

    private OnFragmentInteractionListener mListener;

    private GraphView mGraphView;

    private RainbowLineGraphSeries[] mSeries = new RainbowLineGraphSeries[MAX_AERATOR];

    private SensorDailyDataXML[] sensorXml = new SensorDailyDataXML[MAX_AERATOR];

    DataPoint[] mStateColor = new DataPoint[] {
        new DataPoint(0, Color.RED),
        new DataPoint(3, Color.GREEN),
        new DataPoint(4, Color.RED),
        new DataPoint(5, Color.BLACK),
        new DataPoint(6, Color.RED)
    };





    public AeratorDailyGraphFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AeratorDailyGraphFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AeratorDailyGraphFragment newInstance(String param1, String param2) {
        AeratorDailyGraphFragment fragment = new AeratorDailyGraphFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
            mBaseUrl = getArguments().getString("BASE_URL");
            mSelectedPond = getArguments().getInt("SELECTED_POND", 0);
            mStrGraphGroup = getArguments().getString("GRAPH_GROUP");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_daily_graph, container, false);

        mGraphView = (GraphView) rootView.findViewById(R.id.graph);

        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setGraphFormat() {

        mGraphView.getGridLabelRenderer().setGridColor(Color.DKGRAY);

        mGraphView.getViewport().setXAxisBoundsManual(true);
        mGraphView.getViewport().setMinX(0);
        mGraphView.getViewport().setMaxX(288);

        // set manual Y bounds
        mGraphView.getViewport().setYAxisBoundsManual(true);
        mGraphView.getViewport().setMinY(0);
        mGraphView.getViewport().setMaxY(16);

        mGraphView.getViewport().setScrollable(true);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(mGraphView);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"", "", "", "03", "", "", "06", "", "", "09", "", "", "12", "", "", "15", "", "", "18", "", "", "21", "", "", ""});
        staticLabelsFormatter.setVerticalLabels(new String[]{"", "", "", "", "", "5", "", "", "", "", "10", "", "", "", "", "15", ""});
        mGraphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


        for (int i = 0; i < MAX_AERATOR; i++) {
            mSeries[i] = new RainbowLineGraphSeries<DataPoint>();
            //mSeries[i].setTitle("series 1");
            mSeries[i].setColor(Color.BLUE);
            mSeries[i].setThickness(5);
            mSeries[i].setOffset(i + 1);
            mSeries[i].setFlatLine(true);
            mSeries[i].setLevelColor(mStateColor);
            mGraphView.addSeries(mSeries[i]);
        }
    }

    public void updateSeriesData() {
        {
            for(int i=0;i<MAX_AERATOR;i++) {
                DataPoint[] dataPoint;
                dataPoint = new DataPoint[sensorXml[i].getCountRecord()];

                for (int ii = 0; ii < sensorXml[i].getCountRecord(); ii++) {
                    DataPoint v = new DataPoint(ii, sensorXml[i].getDataValue(ii));
                    dataPoint[ii] = v;
                }
                mSeries[i].resetData(dataPoint);
                mSeries[i].setTitle(sensorXml[i].getIoName());
            }
        }

        mGraphView.getLegendRenderer().setVisible(false);
        mGraphView.getLegendRenderer().setBackgroundColor(Color.WHITE);
        mGraphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

    }

    void setDate(String date_str) {
        mStrSelectedDay = date_str;
    }

    void setGraphGroup(String group_str) {
        mStrGraphGroup = group_str;
    }

    public boolean checkInternetConnection() {

        boolean have_connection = false;

        ConnectivityManager cManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();

        if(nInfo==null) {
            Toast.makeText(getActivity() , "No Internet connection! Please connect to the Internet.", Toast.LENGTH_LONG).show();
        } else {
            have_connection=true;
        }
        return have_connection;
    }

    void updateGraph() {

        if (checkInternetConnection()==false) return;

        DownloadFromInternet Downloader = new DownloadFromInternet();
        Downloader.execute("100", mStrSelectedDay);
    }


    // Async Task Class
    class DownloadFromInternet extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        boolean cancle;

        // Show Progress bar before downloading Music
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Shows Progress Bar Dialog and then call doInBackground method
            //showDialog(progress_bar_type);
            cancle = false;

            progressDialog = ProgressDialog.show(getActivity(),
                    "Downloading Data",
                    "Please Wait!");

            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancle = true;
                }
            });

            //Toast.makeText(getActivity(),"Progress Start",Toast.LENGTH_LONG).show();
        }

        // Download xml from Internet
        @Override
        protected String doInBackground(String... str) {
            int count=1;
            DataPoint[] dataPoint;
            String finalUrl;
            String io_number_str = str[0];
            String date_str = str[1];
            int io_number=1560;
            boolean loading_complete;

            try {

                for (int i=0;i<MAX_AERATOR;i++) {
                    finalUrl = mBaseUrl + ",4096," + io_number + "," + date_str;
                    io_number++;

                    sensorXml[i] = new SensorDailyDataXML(finalUrl);
                    sensorXml[i].fetchXML(finalUrl);
                }

                do {
                    loading_complete=true;
                    for(int i=0;i<MAX_AERATOR;i++) {
                        loading_complete = loading_complete && sensorXml[i].isFetchComplete();
                    }
                        // Publish the progress which triggers onProgressUpdate method
                    publishProgress("" + count++);
                    if (!loading_complete) Thread.sleep(1000);
                } while (!loading_complete);

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        // While Downloading Music File
        @Override
        protected void onProgressUpdate(String... progress) {
            // Set progress percentage
            progressDialog.setMessage("Please wait... " + String.valueOf(progress[0]) + " sec");
        }

        // Once XML is downloaded
        @Override
        protected void onPostExecute(String file_url) {
            // Dismiss the dialog after the Xml file was downloaded
            //Toast.makeText(getActivity(),"Progress Ended",Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            // Play the music
            updateSeriesData();


        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("STR_SELECTED_DAY", mStrSelectedDay);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState!=null) {
            mStrSelectedDay = savedInstanceState.getString("STR_SELECTED_DAY");
            //setGraphFormat();
            //updateSeriesData();
        }
        if (mStrSelectedDay==null) {
            mStrSelectedDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            //setGraphFormat();
            //updateGraph();
        }
        setGraphFormat();
        updateGraph();



    }

    public static Date convertStringToDate(String date) {
        SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            try {
                return FORMATTER.parse(date);
            } catch (ParseException e) {
                // nothing we can do if the input is invalid
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}

