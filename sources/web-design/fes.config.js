import zh from './zh.json';
import en from './en.json';

const lang = window.localStorage.getItem('lang') || 'zh-cn';
export default {
    mode: 'vertical',
    theme: 'dark',
    fesName: 'Exchangis',
    roles: {
        unLogin: ['/ds/newManager', '/', '/login', '/job', '/task', '/help'],
        user: ['/ds/newManager', '/', '/login', '/ds/dataTemplate', '/job', '/task', '/help', '/group', '/group/groupDetail'],
        admin: ['/ds/newManager', '/', '/login', '/ds/dataTemplate', '/job', '/task', '/help', '/group', '/group/groupDetail', '/sys/owner'],
        super: ['/ds/newManager', '/', '/login', '*']
    },
    map: {
        status: [
            ['1', '成功'],
            ['2', '失败']
        ]
    },
    i18n: {
        locale: lang,
        messages: {
            en,
            'zh-cn': zh
        }
    },
    menu: [{
        title: '$i18n.navBar.dataManager',
        path: '/ds',
        subMenu: [{
            title: '$i18n.navBar.dataSource',
            path: '/ds/newManager'
        },
        {
            title: '$i18n.navBar.templaceManagert',
            path: '/ds/dataTemplate'
        }
        ]
    },
    {
        title: '$i18n.navBar.taskConfig',
        path: '/job'
    },
    {
        title: '$i18n.navBar.tasking',
        path: '/task'
    },
    {
        title: '$i18n.navBar.group',
        path: '/group'
    },
    // {
    //     title: '$i18n.navBar.help',
    //     path: '/help'
    // },
    {
        title: '$i18n.navBar.sysManagert',
        path: '/sys',
        subMenu: [{
            title: '$i18n.navBar.owner',
            path: '/sys/owner'
        },
        {
            title: '$i18n.navBar.execotor',
            path: '/sys/executor'
        },
        {
            title: '$i18n.navBar.executeUser',
            path: '/sys/executeUser'
        },
        {
            title: '$i18n.navBar.user',
            path: '/sys/user'
        }
        ]
    }
    ],
    env: {
        local: {
            api: '' // 本地联调地址
        },
        prod: {
            api: '/api/v1' // 生产环境
        },
        open: {
            api: '/api/v1'
        }
    }
};
