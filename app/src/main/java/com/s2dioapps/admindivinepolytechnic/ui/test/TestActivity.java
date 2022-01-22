package com.s2dioapps.admindivinepolytechnic.ui.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.s2dioapps.admindivinepolytechnic.R;
import com.s2dioapps.admindivinepolytechnic.common.DbQuery;
import com.s2dioapps.admindivinepolytechnic.ui.question.QuestionActivity;
import com.s2dioapps.admindivinepolytechnic.ui.question.QuestionAdapter;
import com.s2dioapps.admindivinepolytechnic.ui.question.QuestionModel;
import com.s2dioapps.admindivinepolytechnic.ui.subject.SubjectFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    private RecyclerView testView;
    private Button addSetB;
    private TestAdapter adapter;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog;
    public static int selected_test_index=0;

    public static List<TestModel> testList = new ArrayList<>();

    public static int ctrTest = 0;

    Dialog addTestDialog;
    private EditText dialogTestName;
    private EditText dialogTestTime;
    private Button dialogAddB;


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

        addTestDialog = new Dialog(this);
        addTestDialog.setContentView(R.layout.add_test_dialog);
        addTestDialog.setCancelable(true);
        addTestDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


        dialogTestName = addTestDialog.findViewById(R.id.et_test_name);
        dialogTestTime = addTestDialog.findViewById(R.id.et_test_time);
        dialogAddB = addTestDialog.findViewById(R.id.btn_add_test);

        addSetB.setText("ADD NEW TEST");
        firestore = FirebaseFirestore.getInstance();

        addSetB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogTestName.getText().clear();
                dialogTestTime.getText().clear();
                addTestDialog.show();

            }
        });

        dialogAddB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogTestName.getText().toString().isEmpty())
                {
                    dialogTestName.setError("Enter Test Name");
                    return;
                }else if(dialogTestTime.getText().toString().isEmpty())
                {
                    dialogTestTime.setError("Enter Test Time");
                    return;
                }

                addNewTest(dialogTestName.getText().toString(), Integer.parseInt(dialogTestTime.getText().toString()));
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        testView.setLayoutManager(layoutManager);


        loadSets();

        ctrTest = SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getNoOfTests();


    }
    private void loadSets() {

        testList.clear();

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


                            testList.add(new TestModel(
                                    documentSnapshot.getString("TEST"+ String.valueOf(i) + "_ID"),
                                    0,
                                    documentSnapshot.getLong("TEST"+ String.valueOf(i) + "_TIME").intValue()
                            ));


                        }

                        adapter = new TestAdapter(testList);
                        testView.setAdapter(adapter);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }//end of loadSets


    private void addNewTest(final String title, final int time)
    {

        addTestDialog.dismiss();
        loadingDialog.show();

        WriteBatch batch = firestore.batch();

//        DocumentReference userDoc = DbQuery.g_firestore.collection("Users")
//                                .document(SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getId())
//                                .collection("USER_DATA")
//                                .document("MY_SCORES");
//
//        Map<String,Object> userInfo = new HashMap<>();
//
//        userInfo.put(TestActivity.testList.get(TestActivity.selected_test_index).getTestID(),
//                    FieldValue.delete());
//
//        userDoc.update(userInfo);


        DocumentReference quizDoc = DbQuery.g_firestore.collection("Quiz")
                .document(SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getId());

        Map<String,Object> testInfo = new ArrayMap<>();

        Map<String,Object> subDoc = new ArrayMap<>();
        subDoc.put("CAT" + (SubjectFragment.selected_cat_index + 1) + "_NO_OF_TESTS",
                ++ctrTest);

        testInfo.put("TEST" + ctrTest + "_ID",title);
        testInfo.put("TEST" + ctrTest + "_TIME",time);
        SubjectFragment.catList.get(SubjectFragment.selected_cat_index).setNoOfTests(ctrTest);

        DocumentReference scoreDoc = quizDoc.collection("TEST_LIST").document("TEST_INFO");

        batch.set(scoreDoc, testInfo, SetOptions.merge());

        DbQuery.g_firestore.collection("Quiz")
                .document("Categories")
                .update(subDoc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        testList.add(new TestModel(title,0,time));


                        adapter.notifyItemInserted(testList.size());
                        loadingDialog.dismiss();

                    }
                });


        batch.update(quizDoc,"NO_OF_TESTS",ctrTest);
        batch.commit();


    }

    public static void loadDataQuestion() {
        QuestionActivity.loadingDialog.show();

        QuestionActivity.quesList.clear();

        QuestionActivity.firestore.collection("Question")
                .whereEqualTo("TEST",TestActivity.testList.get(TestActivity.selected_test_index).getTestID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuestionActivity.loadingDialog.dismiss();
                        if(task.isSuccessful())
                        {
                            QuerySnapshot query = task.getResult();
                            for(int i = 0 ; i < query.getDocuments().size(); i++)
                            {

                                String a = query.getDocuments().get(i).getString("A");
                                int answer = query.getDocuments().get(i).getLong("ANSWER").intValue();
                                String b = query.getDocuments().get(i).getString("B");
                                String c = query.getDocuments().get(i).getString("C");
                                String category = query.getDocuments().get(i).getString("CATEGORY");
                                String d = query.getDocuments().get(i).getString("D");
                                String question = query.getDocuments().get(i).getString("QUESTION");
                                String test = query.getDocuments().get(i).getString("TEST");
                                String uid = query.getDocuments().get(i).getString("UID");


                                //Log.e("DUDE",query.getDocuments().get(i).getString("QUESTION"));

                                QuestionActivity.quesList.add(new QuestionModel(a, answer, b, c, category, d, question, test, uid));

                            }

                            QuestionActivity.adapter = new QuestionAdapter(QuestionActivity.quesList);
                            QuestionActivity.quest_recycler_view.setAdapter(QuestionActivity.adapter);

                        }
                    }
                });

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