package com.s2dioapps.admindivinepolytechnic.ui.test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.s2dioapps.admindivinepolytechnic.R;
import com.s2dioapps.admindivinepolytechnic.common.DbQuery;
import com.s2dioapps.admindivinepolytechnic.ui.question.QuestionActivity;
import com.s2dioapps.admindivinepolytechnic.ui.subject.SubjectFragment;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    private List<TestModel> setIDs;

    public TestAdapter(List<TestModel> setIDs) {
        this.setIDs = setIDs;
    }


    @NonNull
    @Override
    public TestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sub_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestAdapter.ViewHolder viewHolder, int i) {

        String setID = setIDs.get(i).getTestID();
        viewHolder.setData(i, setID, this);
    }

    @Override
    public int getItemCount() {
        return setIDs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView setName;
        private ImageView deleteSetB;
        private Dialog loadingDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            setName = itemView.findViewById(R.id.catName);
            deleteSetB = itemView.findViewById(R.id.catDelB);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        }

        private void setData(final int pos, final String setID, final TestAdapter adapter)
        {
            //setName.setText("TEST " + String.valueOf(pos + 1));
            setName.setText(setID);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TestActivity.selected_test_index = pos;

                    Intent intent = new Intent(itemView.getContext(), QuestionActivity.class);
                    itemView.getContext().startActivity(intent);



                }
            });

            deleteSetB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Test")
                            .setMessage("Do you want to delete this test ?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteSet(pos,itemView.getContext(), adapter);
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundColor(Color.RED);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(Color.RED);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,50,0);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);


                }
            });
        }


        private void deleteSet(final int pos, final Context context, final TestAdapter adapter)
        {
            //loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

//            firestore.collection("Question")
//                    .document(FirebaseAuth.getInstance().getUid())
//                    .delete();


            DocumentReference docRef = firestore.collection("Quiz")
                    .document(SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getId())
                    .collection("TEST_LIST")
                    .document("TEST_INFO");

            final Map<String,Object> updates = new HashMap<>();
            updates.put("TEST"+ (pos + 1) +"_ID", FieldValue.delete());
            updates.put("TEST"+ (pos + 1)+"_TIME", FieldValue.delete());



            docRef.update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            firestore.collection("Question")
                                    .whereEqualTo("TEST", TestActivity.testList.get(pos).getTestID())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot document : task.getResult())
                                            {
                                                document.getReference().delete();
                                            }

                                        }
                                    });

                            TestActivity.testList.remove(pos);
                            adapter.notifyDataSetChanged();

                        DocumentReference setDocRef = firestore.collection("Quiz")
                                .document(SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getId())
                                .collection("TEST_LIST")
                                .document("TEST_INFO");

                        final Map<String,Object> setUpdates = new HashMap<>();



                            for(int i = 0; i < TestActivity.testList.size(); i++)
                            {
                                setUpdates.put("TEST"+ (i + 1) +"_ID", TestActivity.testList.get(i).getTestID());
                                setUpdates.put("TEST"+ (i + 1 ) +"_TIME", TestActivity.testList.get(i).getTime());
                                Log.e("HEYE", "count" + (i + 1) + " " + TestActivity.testList.get(i).getTestID());
                            }
                            setDocRef.set(setUpdates);




                        }
                    });



            DocumentReference docTest = firestore.collection("Quiz")
                    .document(SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getId());

            Map<String,Object> test = new HashMap<>();
            test.put("NO_OF_TESTS", --TestActivity.ctrTest);
            docTest.update(test)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            SubjectFragment.catList.get(SubjectFragment.selected_cat_index).setNoOfTests(TestActivity.ctrTest);

                            DocumentReference docCategories = firestore.collection("Quiz")
                                    .document("Categories");

                            Map<String,Object> categories = new HashMap<>();
                            categories.put("CAT"+ (SubjectFragment.selected_cat_index + 1) +"_NO_OF_TESTS", TestActivity.ctrTest);
                            docCategories.update(categories);

                        }
                    });



        }//end of deleteSet

    }
}
