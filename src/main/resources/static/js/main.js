const toHome = () => {
    const token = localStorage.getItem('token');
    $.ajax({
        url: '/home',
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Authorization', `Bearer ${token}`);
        },
        success: () => {
            window.location.href = '/home';
        },
        error: error => {
            if (error.status === 401) {
                const refreshToken = error.getResponseHeader('authorization').slice(7);
                localStorage.setItem('token', refreshToken);
                $.ajax({
                    url: '/main',
                    type: 'GET',
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader('Authorization', `Bearer ${refreshToken}`);
                    },
                    success: () => {
                        window.location.href = '/main';
                    }
                })
            } else {
                console.log(error);
            }
        }
    })
};

const toMain = () => {
    const token = localStorage.getItem('token');
    $.ajax({
        url: '/main',
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Authorization', `Bearer ${token}`);
        },
        success: () => {
            window.location.href = '/main';
        },
        error: error => {
            if (error.status === 401) {
                const refreshToken = error.getResponseHeader('authorization').slice(7);
                localStorage.setItem('token', refreshToken);
                $.ajax({
                    url: '/main',
                    type: 'GET',
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader('Authorization', `Bearer ${refreshToken}`);
                    },
                    success: () => {
                        window.location.href = '/main';
                    }
                })
            } else {
                console.log(error);
            }
        }
    })
};

const logout = function () {
    $.post("/logout", () => {
        window.location.href = '/login';
    });
};
