package com.romoufer.takewater.listadapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.romoufer.takewater.MainActivity;
import com.romoufer.takewater.water.WaterDAO;
import com.romoufer.takewater.notificationreceiver.AlertReceiver;

import java.util.ArrayList;

import com.romoufer.takewater.R;

import static android.content.Context.ALARM_SERVICE;

public class ListAdapterWater extends RecyclerView.Adapter<ListAdapterWater.ViewHolder> {

    Context context;
    ConstraintLayout constraintLayout;
    ArrayList<ListModelWater> listModels;

    public ListAdapterWater(Context context, ConstraintLayout constraintLayout, ArrayList<ListModelWater> listModels) {
        this.context = context;
        this.constraintLayout = constraintLayout;
        this.listModels = listModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txtDate.setText(context.getResources().getString(R.string.date) + " - " + listModels.get(position).getDate());
        holder.txtHour.setText(context.getResources().getString(R.string.schedule) + " - " + listModels.get(position).getHour());
        holder.txtML.setText(context.getResources().getString(R.string.quantidade) + " - " + listModels.get(position).getMl() + " Ml");

        final int id = listModels.get(position).getId();

        holder.btnDeleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WaterDAO waterDAO = new WaterDAO(context);

                LayoutInflater layoutInflater = LayoutInflater.from(context);
                final View linearLayoutConfirmarExclusao = layoutInflater.inflate(R.layout.confirmacao_exclusao_item_database, null);
                linearLayoutConfirmarExclusao.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                constraintLayout.addView(linearLayoutConfirmarExclusao);

                Button btnConfirmarExclusao = (Button) ((Activity) context).findViewById(R.id.btn_confirmar_exclusao);
                Button btnCancelarExclusao = (Button) ((Activity) context).findViewById(R.id.btn_cancelar_exclusao);

                btnConfirmarExclusao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int excluidoComSucesso = waterDAO.excluir(id);

                        if ( excluidoComSucesso == 0 ) {
                            Toast.makeText(context, R.string.excluido_sem_sucesso, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, R.string.excluido_com_sucesso, Toast.LENGTH_SHORT).show();
                        }

                        constraintLayout.removeView(linearLayoutConfirmarExclusao);

                        ((Activity) context).finish();
                        context.startActivity(new Intent(context, context.getClass()));

                    }
                });

                btnCancelarExclusao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        constraintLayout.removeView(linearLayoutConfirmarExclusao);
                    }
                });
            }
        });

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final WaterDAO waterDAO = new WaterDAO(context);

                LayoutInflater layoutInflater = LayoutInflater.from(context);
                final View linearLayoutConfirmarExclusao = layoutInflater.inflate(R.layout.confirmacao_exclusao_item_database, null);
                linearLayoutConfirmarExclusao.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                constraintLayout.addView(linearLayoutConfirmarExclusao);

                Button btnConfirmarExclusao = (Button) ((Activity) context).findViewById(R.id.btn_confirmar_exclusao);
                Button btnCancelarExclusao = (Button) ((Activity) context).findViewById(R.id.btn_cancelar_exclusao);

                btnConfirmarExclusao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int excluidoComSucesso = waterDAO.excluir(id);

                        if ( excluidoComSucesso == 0 ) {
                            Toast.makeText(context, R.string.excluido_sem_sucesso, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, R.string.excluido_com_sucesso, Toast.LENGTH_SHORT).show();
                        }

                        constraintLayout.removeView(linearLayoutConfirmarExclusao);

                        ((Activity) context).finish();
                        context.startActivity(new Intent(context, MainActivity.class));

                    }
                });

                btnCancelarExclusao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        constraintLayout.removeView(linearLayoutConfirmarExclusao);
                    }
                });

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView txtML;
        TextView txtDate;
        TextView txtHour;
        ImageButton btnDeleteData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.layout_adapter_item_list);
            txtML = itemView.findViewById(R.id.text_ml);
            txtDate = itemView.findViewById(R.id.text_data);
            txtHour = itemView.findViewById(R.id.text_hour);
            btnDeleteData = itemView.findViewById(R.id.btn_delete);
        }
    }

}
