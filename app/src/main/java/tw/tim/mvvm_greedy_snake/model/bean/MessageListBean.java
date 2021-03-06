package tw.tim.mvvm_greedy_snake.model.bean;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtm.RtmMessage;
import tw.tim.mvvm_greedy_snake.rtmtutorial.ChatManager;

public class MessageListBean {
    private String accountOther;
    private List<MessageBean> messageBeanList;

    public MessageListBean(String account, List<MessageBean> messageBeanList) {
        this.accountOther = account;
        this.messageBeanList = messageBeanList;
    }

    /**
     * Create message list bean from offline messages  查離線訊息 帶入ID 取得離線訊息
     *
     * @param account     peer user id to find offline messages from
     * @param chatManager chat manager that managers offline message pool
     */
    public MessageListBean(String account, ChatManager chatManager) {
        accountOther = account;
        messageBeanList = new ArrayList<>();

        List<RtmMessage> messageList = chatManager.getAllOfflineMessages(account);
        for (RtmMessage m : messageList) {
            // All offline messages are from peer users
            MessageBean bean = new MessageBean(account, m, false);
            messageBeanList.add(bean);
        }
    }

    public String getAccountOther() {
        return accountOther;
    }

    public void setAccountOther(String accountOther) {
        this.accountOther = accountOther;
    }

    public List<MessageBean> getMessageBeanList() {
        return messageBeanList;
    }

    public void setMessageBeanList(List<MessageBean> messageBeanList) {
        this.messageBeanList = messageBeanList;
    }

}
