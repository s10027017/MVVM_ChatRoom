package tw.tim.mvvm_greedy_snake.view.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    open val list: MutableList<T> = mutableListOf()

    open fun add(t: T) {
        val pos = list.size
        list.add(t)
        notifyItemInserted(pos)
    }

    /**
     * 更新資料
     */
    open fun update(newList: List<T>?) {
        if (newList == null) return
        notifyItemRangeRemoved(0, list.size)
        list.clear()
        list.addAll(newList)
        notifyItemRangeInserted(0, list.size)
    }

    /**
     * @param newList 新的資料
     * 若[newList]有資料則更新資料，否則清空原有資料
     */
    open fun updateOrClear(newList: List<T>?) {
        if (newList == null) {
            notifyItemRangeRemoved(0, list.size)
            list.clear()
            notifyItemRangeInserted(0, list.size)
        }
        else{
            notifyItemRangeRemoved(0, list.size)
            list.clear()
            list.addAll(newList)
            notifyItemRangeInserted(0, list.size)
        }
    }

    open fun remove(t: T) {
        val position = list.indexOf(t)
        if (position < 0) return
        val isRemove = list.remove(t)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}