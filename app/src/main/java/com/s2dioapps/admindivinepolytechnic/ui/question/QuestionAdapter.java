package com.s2dioapps.admindivinepolytechnic.ui.question;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.s2dioapps.admindivinepolytechnic.R;
import com.s2dioapps.admindivinepolytechnic.ui.subject.SubjectFragment;


import java.util.List;
import java.util.Map;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>  {

    private List<QuestionModel> ques_list;

    public QuestionAdapter(List<QuestionModel> ques_list) {
        this.ques_list = ques_list;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sub_item_layout,viewGroup,false);

        return new QuestionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder holder, int position) {

        String title = ques_list.get(position).getQuestion();

        holder.setData(title, position, this);

    }

    public int getItemCount() {
        return ques_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView catName;
        private ImageView deleteB;
        private Dialog loadingDialog;
        private Dialog editDialog;
        private EditText tv_editCatName;
        private Button updateCatB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            catName = itemView.findViewById(R.id.catName);
            deleteB = itemView.findViewById(R.id.catDelB);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            editDialog = new Dialog(itemView.getContext());
            editDialog.setContentView(R.layout.edit_sub_dialog);
            editDialog.setCancelable(true);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


            tv_editCatName = editDialog.findViewById(R.id.ec_cat_name);
            updateCatB = editDialog.findViewById(R.id.ec_add_btn);

        }

        private void setData(String title, final int pos, final QuestionAdapter adapter)
        {
            catName.setText(title);
            //catName.setText("Question " + String.valueOf(pos + 1));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QuestionActivity.selected_ques_index = pos;
                    Intent intent = new Intent(itemView.getContext(), QuestionDetailsActivity.class);

                    intent.putExtra("ACTION","EDIT");
                    intent.putExtra("Q_ID", pos);

                    itemView.getContext().startActivity(intent);
                }
            });

            deleteB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Category")
                            .setMessage("Do you want to delete this category ?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteQuestion(pos, itemView.getContext(), adapter);
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

        }//end of setData

        private void deleteQuestion(final int id, final Context context, final QuestionAdapter adapter)
        {
            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Map<String,Object> catDoc = new ArrayMap<>();
            int index=1;
            for(int i=0; i < ques_list.size(); i++)
            {
                if( i != id)
                {
//                    catDoc.put("CAT" + String.valueOf(index) + "_ID", sub_list.get(i).getId());
//                    catDoc.put("CAT" + String.valueOf(index) + "_NAME", sub_list.get(i).getName());
//                    catDoc.put("CAT" + String.valueOf(index) + "_NO_OF_TESTS", sub_list.get(i).getNoOfTests());
                    index++;
                }else{

                    firestore.collection("Question").document(ques_list.get(i).getUid())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    QuestionActivity.quesList.remove(id);

                                    adapter.notifyDataSetChanged();

                                    loadingDialog.dismiss();
                                }
                            });

                }

            }

        }//end of deleteCategory


    }




}
