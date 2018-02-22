package it.instruman.treasurecruisedatabase;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import it.instruman.treasurecruisedatabase.nakama.network.QueryStageTask;
import it.instruman.treasurecruisedatabase.nakama.network.Stage;

/**
 * Created by infan on 20/02/2018.
 */

public class StagesDialog extends Dialog {
    RecyclerView stagesRecyclerView;
    Context mContext;
    StagesRecyclerAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ProgressBar progressBar;

    public interface OnStageSelected {
        boolean stageSelected(int stageId, String stageName);
    }

    private OnStageSelected onStageSelected;

    public StagesDialog(@NonNull Context context, int themeResId, final OnStageSelected onStageSelected, String stageName) {
        super(context, themeResId);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_stages);

        this.onStageSelected = onStageSelected;

        stagesRecyclerView = findViewById(R.id.stagesRecyclerView);
        stagesRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(mContext);
        stagesRecyclerView.setLayoutManager(mLayoutManager);

        progressBar = findViewById(R.id.progressBar_cyclic);

        QueryStageTask queryStageTask = new QueryStageTask(new QueryStageTask.TaskResult() {
            @Override
            public void onResultsAvailable(List<Stage> results) {
                progressBar.setVisibility(View.GONE);
                if(results==null || results.size()==0)
                    findViewById(R.id.stagesDialogNoneText).setVisibility(View.VISIBLE);
                else {
                    mAdapter = new StagesRecyclerAdapter(results, new OnItemClicked() {
                        @Override
                        public void onClick(int position, int stageId, String stageName) {
                            if (!onStageSelected.stageSelected(stageId, stageName))
                                dismiss();
                        }
                    });
                    stagesRecyclerView.setAdapter(mAdapter);
                }
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        queryStageTask.execute(stageName);
    }

    public interface OnItemClicked {
        void onClick(int position, int stageId, String stageName);
    }

    private class StagesRecyclerAdapter extends RecyclerView.Adapter<StagesRecyclerAdapter.ViewHolder> {
        private List<Stage> mList;

        OnItemClicked onItemClicked;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView stagesStageName;
            public ViewHolder(View itemView) {
                super(itemView);
                stagesStageName = itemView.findViewById(R.id.stagesStageNameTextView);
            }
        }

        public StagesRecyclerAdapter(List<Stage> list, OnItemClicked onItemClicked) {
            setHasStableIds(true);
            mList = list;
            this.onItemClicked = onItemClicked;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.stages_recyclerview_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public long getItemId(int position) {
            return mList.get(position).getStageId();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.stagesStageName.setText(mList.get(position).getStageName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked.onClick(holder.getAdapterPosition(),
                            mList.get(holder.getAdapterPosition()).getStageId(),
                            mList.get(holder.getAdapterPosition()).getStageName());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }
}
