package com.s2dioapps.admindivinepolytechnic.common;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.s2dioapps.admindivinepolytechnic.ui.home.RankModel;
import com.s2dioapps.admindivinepolytechnic.ui.subject.SubjectFragment;
import com.s2dioapps.admindivinepolytechnic.ui.subject.SubjectModel;
import com.s2dioapps.admindivinepolytechnic.ui.test.TestModel;

import java.util.ArrayList;
import java.util.List;

public class DbQuery {

    public static FirebaseFirestore g_firestore;

    public static List<RankModel> g_usersList = new ArrayList<>();

    public static boolean isMeOnTopList = false;

    public static RankModel myPerformance = new RankModel("",0,-1);
    public  static int g_usersCount = 0;

    public static List<TestModel> g_testList = new ArrayList<>();

//    public static List<SubjectModel> g_catList = new ArrayList<>();
//    public static int g_selected_cat_index = 0;

//    public static void loadTestData(MyCompleteListener completeListener)
//    {
//        g_testList.clear();
//
//        g_firestore = FirebaseFirestore.getInstance();
//
//        g_firestore.collection("Quiz").document(SubjectFragment.catList.get(SubjectFragment.selected_cat_index)
//                .getId()).collection("TEST_LIST").document("TEST_INFO")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                        int noOfTests = SubjectFragment.catList.get(SubjectFragment.selected_cat_index).getNoOfTests();
//
//                        for(int i = 1; i <= noOfTests; i++)
//                        {
//                            g_testList.add(new TestModel(
//                                    documentSnapshot.getString("TEST"+ String.valueOf(i) + "_ID"),
//                                    0,
//                                    documentSnapshot.getLong("TEST"+ String.valueOf(i) + "_TIME").intValue()
//                            ));
//
//                        }
//
//                        completeListener.onSuccess();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        completeListener.onFailure();
//                    }
//                });
//    }


    public static void getTopUsers(MyCompleteListener completeListener)
    {
        g_usersList.clear();

        g_firestore = FirebaseFirestore.getInstance();

        String myUID = FirebaseAuth.getInstance().getUid();

        g_firestore.collection("Users")
                .whereGreaterThan(NodeNames.TOTAL_SCORE,0)
                .orderBy(NodeNames.TOTAL_SCORE, Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        int rank = 1;

                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                        {
                            g_usersList.add(new RankModel(
                                    doc.getString("NAME"),
                                    doc.getLong(NodeNames.TOTAL_SCORE).intValue(),
                                    rank
                            ));

                            if(myUID.compareTo(doc.getId()) == 0)
                            {
                                isMeOnTopList = true;
                                myPerformance.setRank(rank);
                            }

                            rank++;
                        }

                        completeListener.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }
}
