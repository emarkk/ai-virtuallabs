export const navHome = {
    name: 'Home', link: '/professor'
};
export const navCourses = {
    name: 'Courses', link: '/professor/courses'
};
export function nav(name: string) {
    return { name };
};