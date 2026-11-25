package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.FormatUtils;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final ArrayList<Product> products;
    private final OnProductClickListener listener;

    public ProductAdapter(ArrayList<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void updateData(ArrayList<Product> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Context context = holder.itemView.getContext();

        holder.tvName.setText(product.getName());
        holder.tvCategory.setText(product.getCategory());
        holder.tvPrice.setText(FormatUtils.formatCurrency(product.getPrice()));
        holder.tvQuantity.setText("Số lượng: " + product.getQuantity());

        if (product.isLowStock()) {
            holder.tvLowStock.setVisibility(View.VISIBLE);
            holder.tvLowStock.setText(product.getQuantity() == 0 ? "Hết hàng" : "Sắp hết hàng");
            holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.error_red));
        } else {
            holder.tvLowStock.setVisibility(View.GONE);
            holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        }

        holder.tvInactive.setVisibility(product.isActive() ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvLowStock;
        final TextView tvCategory;
        final TextView tvInactive;
        final TextView tvPrice;
        final TextView tvQuantity;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvLowStock = itemView.findViewById(R.id.tvItemLowStock);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvInactive = itemView.findViewById(R.id.tvItemInactive);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvItemQuantity);
        }
    }
}
