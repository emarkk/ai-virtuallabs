export const studentFieldsTemplate = field => action => {
  return action.student[field];
};
export const studentLastNameTemplate = action => {
  return action.student.lastName.toUpperCase();
};
export const timestampTemplate = action => {
  return action.date.toLocaleString();
};