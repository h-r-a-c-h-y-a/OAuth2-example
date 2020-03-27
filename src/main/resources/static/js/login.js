
// $("#login").on('click', () => {
//     const name = $('#name').val();
//     const email = $('#email').val;
//     const password = $('#pwd').val;
//     $.post("/auth/signup", {"name": name, "email": email, "password": password}, (data) => {
//         const user = data.user;
//         const token = data.token;
//         localStorage.setItem('token', token);
//         $.get("/home", () => {
//             console.log("success")
//         });
//     }, err => {
//         document.getElementById('err').innerText = err.message;
//         console.log(err);
//     });
// });
$(document).ready(() => {
    $('#register').hide();
    $('#regA').on('click',() => {
        $('#login').show();
        $('#register').hide();
    });
    $('#loginA').on('click', () => {
        $('#register').show();
        $('#login').hide();
    });
});

const register = () => {
    let error = false;
    const name = $('#name').val();
    const email = $('#email').val();
    const password = $('#password').val();
    const confirm = $('#confPwd').val();
    $('.error').html('');
    if (invalidField(name, 'name')) {
        $('#name').attr('placeholder', 'name is required');
        error = true;
    }
    if (invalidField(email, 'email')) {
        $('#email').attr('placeholder', 'invalid email');
        error = true;
    }
    if (invalidField(password, 'password')) {
        $('#pwd').attr('placeholder', 'password is required');
        error = true;
    }
    if (invalidField(confirm, 'password')) {
        $('#confPwd').attr('placeholder', 'replay your password');
        error = true;
    } else if (confirm !== password) {
        $('.error').html('password not matches');
        error = true;
    }
    if (error) return;
    $.ajax({
        url: "/auth/register",
        type: "POST",
        data: {"name": name, "email": email, "password": password},
        dataType: 'json',
        success: (data) => {
            const user = data.user;
            const token = data.token;
            localStorage.setItem('token', token);
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
                    console.log(error);
                }
            })
        },
        error: error => {
            document.getElementById('err').innerText = `user by email: ${email} already exist`;
            console.log(error);
        }
    });
};

const login = () => {
    let error = false;
    const email = $('#em').val();
    const password = $('#pwd').val();
    $('.error').html('');
    if (invalidField(email, 'email')) {
        $('#em').attr('placeholder', 'invalid email');
        error = true;
    }
    if (invalidField(password, 'password')) {
        $('#pwd').attr('placeholder', 'password is required');
        error = true;
    }
    if (error) return;
    $.ajax({
        url: "/auth/signup",
        type: "POST",
        data: {"email": email, "password": password},
        dataType: 'json',
        success: (data) => {
            const user = data.user;
            const token = data.token;
            localStorage.setItem('token', token);
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
                    console.log(error);
                }
            })
        },
        error: error => {
            console.log(error);
        }
    });
};


function invalidField(field, type) {
    if (type === 'email') {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return !re.test(String(field).toLowerCase());
    }
    return field.trim() === '';
}
