package com.moe.adapter;
import java.util.List;
import com.moe.adapter.EmojiAdapter.ViewHolder;
import com.moe.utils.ImageCache;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.moe.yaohuo.R;
import android.support.v7.widget.RecyclerView;
public class ImagesAdapter extends EmojiAdapter
{
	private List<String> list;
	public ImagesAdapter(List<String> list){
		super(list);
		this.list=list;
	}

	@Override
	public EmojiAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.face,p1,false));
	}
	
	@Override
	public void onBindViewHolder(EmojiAdapter.ViewHolder vh, int p2)
	{
		Glide.with(vh.icon.getContext()).load(list.get(p2)).diskCacheStrategy(DiskCacheStrategy.ALL).into(vh.icon);
	}
	public class ViewHolder extends EmojiAdapter.ViewHolder implements View.OnLongClickListener{
		public ViewHolder(View v){
			super(v);
			v.setOnLongClickListener(this);
		}

		@Override
		public boolean onLongClick(View p1)
		{
			if(oilcl!=null)return oilcl.onItemLongClick(ImagesAdapter.this,this);
			return false;
		}

		
	}
	public void setOnItemLongClickListener(OnItemLongClickListener l){
		oilcl=l;
	}
	private OnItemLongClickListener oilcl;
	public abstract interface OnItemLongClickListener{
		boolean onItemLongClick(RecyclerView.Adapter adapter,RecyclerView.ViewHolder vh);
	}
}
