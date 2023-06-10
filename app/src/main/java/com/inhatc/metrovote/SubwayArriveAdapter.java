package com.inhatc.metrovote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inhatc.metrovote.api.SubwayArriveDTO;

import java.text.SimpleDateFormat;
import java.util.List;

public class SubwayArriveAdapter extends RecyclerView.Adapter<SubwayArriveAdapter.ViewHolder> {

    private List<SubwayArriveDTO> subwayArriveList;

    public SubwayArriveAdapter(List<SubwayArriveDTO> subwayArriveList) {
        this.subwayArriveList = subwayArriveList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subway_arrive, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubwayArriveDTO subwayArriveDTO = subwayArriveList.get(position);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        holder.txtLineNum.setText(subwayArriveDTO.getLineNum());
        holder.txtTrainNo.setText(subwayArriveDTO.getTrainNo());
        String arriveTime = format.format(subwayArriveDTO.getArriveTime());
        if(arriveTime.equals("00:00:00")) {
            holder.txtArriveTime.setText("종점 출발");
        } else {
            holder.txtArriveTime.setText(arriveTime);
        }
        holder.txtLeftTime.setText(format.format(subwayArriveDTO.getLeftTime()));
        holder.txtSubwaySName.setText(subwayArriveDTO.getSubwaysName());
        holder.txtSubwayEName.setText(subwayArriveDTO.getSubwayEName());
        holder.txtInoutTag.setText(subwayArriveDTO.getInoutTag());
        holder.txtFLFlag.setText(subwayArriveDTO.getFlFlag());

    }

    @Override
    public int getItemCount() {
        return subwayArriveList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtLineNum;
        public TextView txtTrainNo;
        public TextView txtArriveTime;
        public TextView txtLeftTime;
        public TextView txtSubwaySName;
        public TextView txtSubwayEName;
        public TextView txtInoutTag;
        public TextView txtFLFlag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtLineNum     = itemView.findViewById(R.id.txtLineNum);
            txtTrainNo     = itemView.findViewById(R.id.txtTrainNo);
            txtArriveTime  = itemView.findViewById(R.id.txtArriveTime);
            txtLeftTime    = itemView.findViewById(R.id.txtLeftTime);
            txtSubwaySName = itemView.findViewById(R.id.txtSubwaysName);
            txtSubwayEName = itemView.findViewById(R.id.txtSubwayEName);
            txtInoutTag    = itemView.findViewById(R.id.txtInoutTag);
            txtFLFlag      = itemView.findViewById(R.id.txtFlFlag);
        }
    }
}
