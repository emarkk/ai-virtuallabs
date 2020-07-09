export const navHome = {
    name: 'Home', link: '/student'
};
export const navCourses = {
    name: 'Courses', link: '/student/courses'
};
export function nav(name: string, link?: string) {
    return { name, link };
};