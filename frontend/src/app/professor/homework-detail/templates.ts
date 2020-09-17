import { HomeworkAction, HomeworkActionType } from 'src/app/core/models/homework-action.model';

export const studentFieldsTemplate = field => (action: HomeworkAction) => {
  return action.student[field];
};
export const studentLastNameTemplate = (action: HomeworkAction) => {
  return action.student.lastName.toUpperCase();
};
export const statusTemplate = (action: HomeworkAction) => {
  if(action.actionType == HomeworkActionType.NULL)
    return `🕒 Unread`;
  if(action.actionType == HomeworkActionType.READ)
    return `✔️ Read`;
  if(action.actionType == HomeworkActionType.DELIVERY)
    return `📓 Submitted`;
  if(action.actionType == HomeworkActionType.REVIEW && action.mark == null)
    return `💬 Reviewed`;
  if(action.actionType == HomeworkActionType.REVIEW && action.mark != null)
    return `⭐ Evaluated`;
};
export const timestampTemplate = (action: HomeworkAction) => {
  return action.date.toLocaleString();
};