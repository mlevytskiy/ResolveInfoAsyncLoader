package appicon.funakoshi.com.apploadiconasync;

import android.content.*;
import android.content.pm.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.funakoshi.resolveInfoAsyncLoader.IconImageView;
import com.funakoshi.resolveInfoAsyncLoader.LabelTextView;

import java.util.List;

/**
 * Created by max on 30.09.15.
 */
public class AppsAdapter extends BaseAdapter {

    private List<ResolveInfo> list;
    private LayoutInflater inflater;

    public AppsAdapter(List<ResolveInfo> list, Context context) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ResolveInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_app, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.fill(getItem(position));
        return convertView;
    }

    private static class ViewHolder {

        LabelTextView label;
        IconImageView icon;

        public ViewHolder(View view) {
            label = (LabelTextView) view.findViewById(R.id.text_view);
            icon = (IconImageView) view.findViewById(R.id.image_view);
        }

        void fill(ResolveInfo resolveInfo) {
            label.setResolveInfo(resolveInfo);
            icon.setResolveInfo(resolveInfo);
        }
    }
}
