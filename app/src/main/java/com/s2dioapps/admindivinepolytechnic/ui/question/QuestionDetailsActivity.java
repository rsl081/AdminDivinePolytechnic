package com.s2dioapps.admindivinepolytechnic.ui.question;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.s2dioapps.admindivinepolytechnic.R;
import com.s2dioapps.admindivinepolytechnic.ui.subject.SubjectFragment;
import com.s2dioapps.admindivinepolytechnic.ui.test.TestActivity;

import java.util.Map;

public class QuestionDetailsActivity extends AppCompatActivity {


    private EditText ques, optionA, optionB, optionC, optionD, answer;
    private Button addQB;
    private String qStr, aStr, bStr, cStr, dStr, ansStr;
    private Dialog loadingDialog;
    private FirebaseFirestore firestore;
    private String action;
    private int qID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);


        Toolbar toolbar = findViewById(R.id.qdetails_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ques = findViewById(R.id.question);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        answer = findViewById(R.id.answer);
        addQB = findViewById(R.id.addQB);


        loadingDialog = new Dialog(QuestionDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


        firestore = FirebaseFirestore.getInstance();

        action = getIntent().getStringExtra("ACTION");

        if(action.compareTo("EDIT") == 0)
        {
            qID = getIntent().getIntExtra("Q_ID",0);
            loadData(qID);
            getSupportActionBar().setTitle("Question " + String.valueOf(qID + 1));
            addQB.setText("UPDATE");
        }
        else
        {
            getSupportActionBar().setTitle("Question " + String.valueOf(QuestionActivity.quesList.size() + 1));
            addQB.setText("ADD");
        }

        addQB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                qStr = ques.getText().toString();
                aStr = optionA.getText().toString();
                bStr = optionB.getText().toString();
                cStr = optionC.getText().toString();
                dStr = optionD.getText().toString();
                ansStr = answer.getText().toString();

                if(qStr.isEmpty()) {
                    ques.setError("Enter Question");
                    return;
                }

                if(aStr.isEmpty()) {
                    optionA.setError("Enter option A");
                    return;
                }

                if(bStr.isEmpty()) {
                    optionB.setError("Enter option B ");
                    return;
                }
                if(cStr.isEmpty()) {
                    optionC.setError("Enter option C");
                    return;
                }
                if(dStr.isEmpty()) {
                    optionD.setError("Enter option D");
                    return;
                }
                if(ansStr.isEmpty()) {
                    answer.setError("Enter correct answer");
                    return;
                }

                if(action.compareTo("EDIT") == 0)
                {
                    editQuestion();
                }
                else {
                    addNewQuestion();
                }

            }
        });


    }

    private void editQuestion() {
        loadingDialog.show();

        final String ques_id = firestore.collection("Question").document().getId();

        Map<String,Object> quesData = new ArrayMap<>();

        quesData.put("QUESTION",qStr);
        quesData.put("A",aStr);
        quesData.put("B",bStr);
        quesData.put("C",cStr);
        quesData.put("D",dStr);
        quesData.put("ANSWER",Integer.parseInt(ansStr));
        quesData.put("CATEGORY", SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getId());
        quesData.put("TEST", TestActivity.testList.get(TestActivity.selected_test_index).getTestID());
        //quesData.put("UID",ques_id);

        firestore.collection("Question").document(QuestionActivity.quesList.get(qID).getUid())
                .update(quesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        QuestionActivity.quesList.get(qID).setOptionA(aStr);
                        QuestionActivity.quesList.get(qID).setCorrectAns(Integer.parseInt(ansStr));
                        QuestionActivity.quesList.get(qID).setOptionB(bStr);
                        QuestionActivity.quesList.get(qID).setOptionC(cStr);
                        QuestionActivity.quesList.get(qID).setCategory(SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getId());
                        QuestionActivity.quesList.get(qID).setOptionD(dStr);
                        QuestionActivity.quesList.get(qID).setQuestion(qStr);
                        QuestionActivity.quesList.get(qID).setTest(TestActivity.testList.get(TestActivity.selected_test_index).getTestID());
                        QuestionActivity.quesList.get(qID).setUid(ques_id);



                        QuestionActivity.adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                        QuestionDetailsActivity.this.finish();
                    }
                });

    }

    private void addNewQuestion() {
        loadingDialog.show();

        final String ques_id = firestore.collection("Question").document().getId();

        Map<String,Object> quesData = new ArrayMap<>();

        quesData.put("QUESTION",qStr);
        quesData.put("A",aStr);
        quesData.put("B",bStr);
        quesData.put("C",cStr);
        quesData.put("D",dStr);
        quesData.put("ANSWER",Integer.parseInt(ansStr));
        quesData.put("CATEGORY", SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getId());
        quesData.put("TEST", TestActivity.testList.get(TestActivity.selected_test_index).getTestID());
        quesData.put("UID",ques_id);

        firestore.collection("Question").document(ques_id).set(quesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {


                        QuestionActivity.quesList.add(
                                new QuestionModel(
                                        aStr, Integer.parseInt(ansStr), bStr,
                                        cStr, SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getId(),
                                        dStr, qStr, TestActivity.testList.get(TestActivity.selected_test_index).getTestID(),
                                        ques_id));

                        QuestionActivity.adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                        QuestionDetailsActivity.this.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionDetailsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });



    }


    private void loadData(int id)
    {
        ques.setText(QuestionActivity.quesList.get(id).getQuestion());
        optionA.setText(QuestionActivity.quesList.get(id).getOptionA());
        optionB.setText(QuestionActivity.quesList.get(id).getOptionB());
        optionC.setText(QuestionActivity.quesList.get(id).getOptionC());
        optionD.setText(QuestionActivity.quesList.get(id).getOptionD());
        answer.setText(String.valueOf(QuestionActivity.quesList.get(id).getCorrectAns()));
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(QuestionActivity.adapter != null) {
            QuestionActivity.adapter.notifyDataSetChanged();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



}