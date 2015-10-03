package appicon.funakoshi.com.apploadiconasync;

import android.content.*;
import android.content.pm.*;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by max on 30.09.15.
 */
public class AppsAdapterWithLags extends BaseAdapter {

    private List<ResolveInfo> list;
    private LayoutInflater inflater;
    private PackageManager packageManager;

    public AppsAdapterWithLags(List<ResolveInfo> list, Context context) {
        this.list = list;
        inflater = LayoutInflater.from(context);
        packageManager = context.getPackageManager();
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
            convertView = inflater.inflate(R.layout.item_app2, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ResolveInfo resolveInfo = getItem(position);
        viewHolder.fill(resolveInfo, resolveInfo.loadLabel(packageManager).toString(), resolveInfo.loadIcon(packageManager));
        return convertView;
    }

    private static class ViewHolder {

        TextView label;
        ImageView icon;

        public ViewHolder(View view) {
            label = (TextView) view.findViewById(R.id.text_view);
            icon = (ImageView) view.findViewById(R.id.image_view);
        }

        void fill(ResolveInfo resolveInfo, String labelStr, Drawable iconDrawable) {
            label.setText(labelStr);
            icon.setImageDrawable(iconDrawable);
        }
    }
}
