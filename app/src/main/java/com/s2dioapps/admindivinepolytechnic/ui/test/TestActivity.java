package com.s2dioapps.admindivinepolytechnic.ui.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.s2dioapps.admindivinepolytechnic.R;
import com.s2dioapps.admindivinepolytechnic.ui.subject.SubjectFragment;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private RecyclerView testView;
    private Button addSetB;
    private TestAdapter adapter;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog;

    public static List<TestModel> setsIDs = new ArrayList<>();
    public static int selected_set_index=0;

    public static List<TestModel> g_testList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toolbar toolbar = findViewById(R.id.sa_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tests");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        testView = findViewById(R.id.sets_recycler);
        addSetB = findViewById(R.id.addSetB);

        loadingDialog = new Dialog(TestActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addSetB.setText("ADD NEW TEST");

        addSetB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNewSet();

            }
        });


        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        testView.setLayoutManager(layoutManager);


        loadSets();


    }
    private void loadSets() {
        setsIDs.clear();

        loadingDialog.show();

        firestore.collection("Quiz").document(SubjectFragment.catList.get(SubjectFragment.selected_cat_index)
                .getId()).collection("TEST_LIST").document("TEST_INFO")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        loadingDialog.dismiss();

                        int noOfTests = SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getNoOfTests();

                        for(int i = 1; i <= noOfTests; i++)
                        {


                            g_testList.add(new TestModel(
                                    documentSnapshot.getString("TEST"+ String.valueOf(i) + "_ID"),
                                    0,
                                    documentSnapshot.getLong("TEST"+ String.valueOf(i) + "_TIME").intValue()
                            ));


                        }

                        Log.e("Hey", String.valueOf(g_testList));
//                        adapter = new TestAdapter(setsIDs);
//                        testView.setAdapter(adapter);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }//end of loadSets

    private void addNewSet()
    {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            TestActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}