import 'assets/styles/index.scss';
export default function () {

    /**
     * axios 全局过滤器
     */
    const roleName = (val) => {
        if (val === 'super') {
            return '超级用户';
        } else if (val === 'admin') {
            return '管理用户';
        } else if (val === 'user') {
            return '普通用户'
        } else {
            return '未登录'
        }
    }
    this.FesApi.option({
        config: {
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Content-language': window.localStorage.getItem('lang') || 'zh-cn'
            }
        }
    });
    const that = this;

    let api = this.FesApi;
    this.FesApp.setBeforeRouter(function (from, to, next) {
        // 分组的信息的修改，当有修改弹窗提示是否保存，将需跳转的路径传入分组页，确认是否保存操作
        if (to.path === '/group/groupDetail' && this.FesFesx.get('isChange')) {
            this.FesFesx.set('isGoto', from.path);
            // next()
            return
        }
        this.FesFesx.set('isGoto', '');
        console.log(to.path, from.path, 'tofrom')
        if (from.path !== '/login') {
            api.fetch('/auth', {}, 'get').then((res) => {
                next();
            })
        } else {
            next();
        }
    });
    this.FesApi.setResponse({
        successCode: 0,
        codePath: 'code',
        messagePath: 'message',
        resultPath: 'data'
    });
    this.FesApi.setImportant({
        'generalcard/action': {
            control: 10000,
            message: '您在十秒内重复发起手工清算操作，是否继续？'
        }
    });
    this.FesApi.setError({
        302: function (response) {
            let res = response.data;
            that.FesApp.router.push('/login');
        },
        401: function (response) {
            window.Toast('未授权操作')
        }
    });
    this.on('fes_logout', () => {
        api.fetch('/logout', {}, 'post').then(() => {
            console.log('[退出成功]');
        }).catch(()=>{
            // window.Toast('登出系统失败');
        })
    })
    api.fetch('/auth', {}, 'get').then((res) => {
        if (typeof res.redirect !== 'undefined') {
        } else {
            if (typeof res['X-AUTH-ID'] !== 'undefined') {
                this.FesApp.setRole(res.role, true, false);
                this.FesApp.set('FesUserName', res['X-AUTH-ID']);
                this.FesApp.set('FesRoleName', roleName(res.role));
                this.FesStorage.set('currentUser', res['X-AUTH-ID']);
                this.FesStorage.set('userRole', res.role);
            } else {
                this.setRole('unLogin');
                this.FesStorage.set('currentUser', 'guest');
                this.FesStorage.set('userRole', 'unLogin');
            }
        }
    }).catch(e => {
        console.error(e);
    });
    this.set('FesLogoEvent', () => {
        window.Toast('WeDataSphere Exchangis');
    });
}