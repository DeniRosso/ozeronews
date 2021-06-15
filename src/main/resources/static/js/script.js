
//$('.menu-btn').on('click', function() {
//    $('.menu').toggleClass('menu_active');
//});

// Choice theme
window.addEventListener('load', function () {
    if (!localStorage.theme) {
        document.documentElement.setAttribute('theme', 'dark');
        localStorage.setItem('theme', 'dark');
    }
    if (localStorage.theme == 'dark') {
        btnToggleTheme.checked = true;
        document.documentElement.setAttribute('theme', 'dark');
    } else {
        btnToggleTheme.checked = false;
        document.documentElement.setAttribute('theme', 'light');
    }
})
if (document.getElementById('btnToggleTheme') !== null) {
    document.getElementById('btnToggleTheme').onclick = function(e) {
    var imgToggleTheme = document.getElementById('imgToggleTheme');
        if (localStorage.theme == 'light') {
            document.documentElement.setAttribute('theme', 'dark');
            localStorage.setItem('theme', 'dark');
            imgToggleTheme.src = "/static/images/theme_light.png";
        } else {
            document.documentElement.setAttribute('theme', 'light');
            localStorage.setItem('theme', 'light');
            imgToggleTheme.src = "/static/images/theme_dark.png";
        }
    }
}

// Main menu
if (document.querySelector('.main-menu-btn') !== null) {
    document.querySelector('.main-menu-btn').onclick = function(e) {
        e.preventDefault;
        document.querySelector('.main-menu-btn-burger').classList.toggle('active');
        document.querySelector('.main-menu').classList.toggle('active');
        document.querySelector('body').classList.toggle('lock');
    }
}

if (document.querySelector('.main-menu-btn-dropdown') !== null) {
    document.querySelector('.main-menu-btn-dropdown').onclick = function(e) {
        e.preventDefault;
        document.querySelector('.main-menu-dropdown-content').classList.toggle('active');

    }
}

// Поиск
if (document.getElementById('btnSearchArticles') != null) {
    document.getElementById('btnSearchArticles').disabled = 'disabled';
    function checkFormSearch() {
        var search = document.getElementById('fldSearch').value.length;
        if (search > 0) {
            document.getElementById('btnSearchArticles').disabled = false;
        }
    }
}

// Load News Page
window.addEventListener('load', function () {

    var id = null;
    var search = null;
    var numberPage = 0;
    var sizePage = 10;
    var pathURLArray = window.location.pathname.split('/');
    var currentPage = pathURLArray[1];
    var articleId = window.location.search.match(new RegExp('id' + '=([^&=]+)'));

    if (currentPage == 'news' || currentPage == 'subscriptions') {
        document.getElementById('articles').innerHTML = "";
        document.getElementById('message').innerHTML = "";
        if (articleId != null) { //undefined
            id = articleId[1];
            search = null;
            loadArticles(id, search, numberPage, sizePage);
        } else {
            id = null;
            search = null;
            loadArticles(id, search, numberPage, sizePage);
        }

    }

    if (document.querySelector('#btnLoadArticles') !== null) {
        document.querySelector('#btnLoadArticles').onclick = function(e) {
            numberPage++;
            loadArticles(id, search, numberPage, sizePage);
            return false;
        }
    }

    if (document.getElementById('btnSearchArticles') != null) {
        document.getElementById('btnSearchArticles').onclick = function(e) {
            search = document.getElementById('formSearch').elements.search.value;
            if (search.length > 0) {
                document.getElementById('articles').innerHTML = "";
                numberPage = 0;
                sizePage = 10;
                loadArticles(id, search, numberPage, sizePage);
                document.getElementById('formSearch').reset();
                document.getElementById('message').innerHTML = "";
                return false;
            } else {
                alert('Для поиска введите значение.');
            }
        }

        document.getElementById('fldSearch').addEventListener('keydown', function(e) {
            if (e.keyCode === 13) {
                search = document.getElementById('formSearch').elements.search.value;
                if (search.length > 0) {
                    document.getElementById('articles').innerHTML = "";
                    numberPage = 0;
                    sizePage = 10;
                    loadArticles(id, search, numberPage, sizePage);
                    document.getElementById('formSearch').reset();
                    document.getElementById('message').innerHTML = "";
                    return false;
                } else {
                  alert('Для поиска введите значение.');
                }
            }
        });
    }

    if (document.querySelector('#btnSizePage10') != null) {
        document.querySelector('#btnSizePage10').onclick = function(e) {
            sizePage = 10;
            numberPage = 0;
            document.querySelector('#btnSizePage10').classList.add('active');
            document.querySelector('#btnSizePage30').classList.remove('active');
            document.querySelector('#btnSizePage50').classList.remove('active');
            document.querySelector('#btnSizePage100').classList.remove('active');
            document.getElementById('articles').innerHTML = "";
            loadArticles(id, search, numberPage, sizePage);
        }
    }

    if (document.querySelector('#btnSizePage30') != null) {
        document.querySelector('#btnSizePage30').onclick = function(e) {
            sizePage = 30;
            numberPage = 0;
            document.querySelector('#btnSizePage10').classList.remove('active');
            document.querySelector('#btnSizePage30').classList.add('active');
            document.querySelector('#btnSizePage50').classList.remove('active');
            document.querySelector('#btnSizePage100').classList.remove('active');
            document.getElementById('articles').innerHTML = "";
            loadArticles(id, search, numberPage, sizePage);
        }
    }

    if (document.querySelector('#btnSizePage50') != null) {
        document.querySelector('#btnSizePage50').onclick = function(e) {
            sizePage = 50;
            numberPage = 0;
            document.querySelector('#btnSizePage10').classList.remove('active');
            document.querySelector('#btnSizePage30').classList.remove('active');
            document.querySelector('#btnSizePage50').classList.add('active');
            document.querySelector('#btnSizePage100').classList.remove('active');
            document.getElementById('articles').innerHTML = "";
            loadArticles(id, search, numberPage, sizePage);
        }
    }

    if (document.querySelector('#btnSizePage100') != null) {
        document.querySelector('#btnSizePage100').onclick = function(e) {
            sizePage = 100;
            numberPage = 0;
            document.querySelector('#btnSizePage10').classList.remove('active');
            document.querySelector('#btnSizePage30').classList.remove('active');
            document.querySelector('#btnSizePage50').classList.remove('active');
            document.querySelector('#btnSizePage100').classList.add('active');
            document.getElementById('articles').innerHTML = "";
            loadArticles(id, search, numberPage, sizePage);
        }
    }
})

// Добавление статуей на страницу news
function loadArticles(id, search, numberPage, sizePage) {
//    var currentURL = window.location.href;
    var baseURL = window.location.protocol + '//' + window.location.host;
    var pathURLArray = window.location.pathname.split('/');
    var currentPage = pathURLArray[1];
    var section = pathURLArray[2];
    var name = pathURLArray[3];

    // Load first group articles
    var xhr = new XMLHttpRequest();

    if (id !== null && id !== '') {
        var url = baseURL + '/news/id' + '?currentPage=' + currentPage +
            '&id=' + id + '&page=' + numberPage + '&size=' + sizePage;
    } else if (search != null && search != '') {
        var url = baseURL + '/news/search' + '?currentPage=' + currentPage + '&section=' + section + '&name=' + name +
            '&search=' + search + '&page=' + numberPage + '&size=' + sizePage;
    } else {
        var url = baseURL + '/news' + '?currentPage=' + currentPage + '&section=' + section + '&name=' + name +
            '&page=' + numberPage + '&size=' + sizePage;
    }

    xhr.open('GET', url);
    xhr.send();
//        xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function(e) {

        var newArticleGroups = document.createElement('DIV');

        var jsonRequest = JSON.parse(xhr.responseText);

        if (jsonRequest.length === 0) {
            var newMessage = document.createElement('DIV');
            newMessage.innerHTML =
            ' <div class="alert alert-danger" role="alert"> ' +
            ' Статья не найдена '
            ' </div> ';
            document.getElementById('message').appendChild(newMessage);
        }

        for (var i = 0; i < jsonRequest.length; i++) {

            var newCard = document.createElement('DIV');
//            newCard.classList.add('card');
            newCard.classList.add('p-sm-2');
            newCard.classList.add('card-article');

            var id = jsonRequest[i].id;
            var title = jsonRequest[i].title;
            var link = jsonRequest[i].link;
            var image = jsonRequest[i].image;
            var resourceKey = jsonRequest[i].resourceId.resourceKey;
            var resourceFullName = jsonRequest[i].resourceId.fullName;
            var resourceShortName = jsonRequest[i].resourceId.shortName;

            if (jsonRequest[i].image != null ) {
                var image = jsonRequest[i].image;
            } else {
                var image = '/static/images/' + resourceKey + '.png';
            }

            var periodPublication = '';
            var publicationDate = new Date(jsonRequest[i].datePublication);

            var currentDate = new Date();
            var months = ['Января', 'Февраля', 'Марта', 'Апреля', 'Мая', 'Июня', 'Июля', 'Августа',
                        'Сентября', 'Октября', 'Ноября', 'Девабря'];
            var year = publicationDate.getFullYear();
            var month = (publicationDate.getMonth() + 1) < 10 ? '0' + (publicationDate.getMonth() + 1)  : (publicationDate.getMonth() + 1);
            var day = publicationDate.getDate() < 10 ? '0' + publicationDate.getDate() : publicationDate.getDate();
            var hours = publicationDate.getHours() < 10 ? '0' + publicationDate.getHours() : publicationDate.getHours();
            var minutes = publicationDate.getMinutes() < 10 ? '0' + publicationDate.getMinutes() : publicationDate.getMinutes();

            dayPublication = publicationDate.getDate() + ' ' + months[publicationDate.getMonth()];
            timePublication = day + '.' + month + '.' + year + ' ' + hours + ':' + minutes;

            var periodMinutes = (currentDate.getTime() - publicationDate.getTime())/(1000 * 60);
            if (periodMinutes >= 0 && periodMinutes < 60) {
                periodPublication = Math.floor(periodMinutes) + 'm';
            }  else if (periodMinutes >= 60 && periodMinutes <= 1440) {
                periodPublication= Math.floor(periodMinutes/60) + 'h';
            } else if (periodMinutes >= 1440 && periodMinutes <= 14400) {
                periodPublication = Math.floor(periodMinutes/(60*24)) + 'd';
            } else {
                periodPublication = '';
            }

            // Создаем блок rubrics
            var rubrics = '';
            for (var j = 0; j < jsonRequest[i].articleRubric.length; j++) {
                var rubricKey = jsonRequest[i].articleRubric[j].rubricId.rubricKey;
                var aliasName = jsonRequest[i].articleRubric[j].rubricId.aliasName;
                if (rubricKey == null) {
                    rubrics = rubrics +
                    '<div class="col-auto p-0 my-1">' +
                        '<a href="#" class="card-article-rubric">' +
                            '<p class="card-article-rubric-txt">' + aliasName + '</p>' +
                        '</a>' +
                    '</div>'
                } else {
                    rubrics = rubrics +
                    '<div class="col-auto p-0 my-1">' +
                        '<a href="/news/rubrics/' + rubricKey + '" class="card-article-rubric active">' +
                            '<p class="card-article-rubric-txt">' + aliasName + '</p>' +
                        '</a>' +
                    '</div>'
                }
            };

            // Создаем блок article
            newCard.innerHTML =
            '<div class="row g-0">' +
                '<div class="col me-1 me-sm-2 d-flex align-items-start flex-column">' +
                    '<div class="card-body p-0 mb-auto w-100 d-flex align-items-start flex-column">' +
                        '<span class="article-id" hidden style="color: #FFF">' + id + '</span>' +
                        '<a href="' + link + '" target="_blank" class="text-decoration-none">' +
                            '<h5 class="card-title card-article-ttl">' + title + '</h5>' +
                        '</a>' +
                        '<div class="row d-inline-flex g-1 p-0 m-0 my-auto w-100 align-items-center justify-content-end" style="max-height: 28px; overflow-x: auto;">' +
                            rubrics +
                        '</div>' +
                    '</div>' +
                    '<div class="card-article-footer">' +
                        '<div class="d-flex align-self-center justify-content-start">' +
                            '<span class="card-article-period">' + periodPublication + '</span>' +
                            '<p class="m-0 my-auto mx-1 card-article-footer-txt">' + dayPublication + '</p>' +
                            '<p class="ml-2 my-auto card-article-footer-txt" style="display: none;"><i class="far fa-bookmark"></i></i></p>' +
                            '<a href="#" id="btnShare_' + id +'" onclick="shareArticle(this)" class="ml-2 my-auto me-auto card-article-footer-txt"><i class="fas fa-share-square"></i></a>' +
                            '<a href="/news/resources/' + resourceKey + ' " class="my-auto text-decoration-none">' +
                                '<p class="m-0 text-right card-article-footer-txt">' + resourceShortName + '</p>' +
                            '</a>' +
                            '<div class="card-article-footer-border"></div>' +
                            '<p class="m-0 my-auto card-article-footer-txt">' + timePublication + '</p>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
                '<div class="col-auto d-flex align-items-start justify-content-center">' +
                    '<div class="card-article-img" style="background:url(' + image + '); background-position: center; background-size: cover;"' +
                    '</div>' +
                '</div>' +
            '</div>';

            newArticleGroups.appendChild(newCard);
        }
        document.getElementById('articles').appendChild(newArticleGroups);
    };
};

// Share article
window.addEventListener('load', function () {
    var shareScreenStatus = 'off';

    shareArticle = function (sender) {

        var baseURL = window.location.protocol + '//' + window.location.host;
        let shareScreen = document.getElementById('shareArticle');
        let shareScreenBackground = document.getElementById('shareBackground');
        let buttonShare = document.getElementById(sender.id).getBoundingClientRect();
        var id = sender.id.slice(sender.id.indexOf('_') + 1);

        var link = baseURL + '/news/news/all' + '?id=' + id;

        if (shareScreenStatus == 'off') {
            shareScreenBackground.style.display = 'block';
            shareScreen.style.top = buttonShare.top + 18 + 'px' ;
            shareScreen.style.left = buttonShare.left + 'px';
            shareScreen.style.display = 'block';
            shareScreenStatus = 'on';
        } else {
            shareScreenBackground.style.display = 'none';
            shareScreen.style.display = 'none';
            shareScreenStatus = 'off';
        }

        shareScreenBackground.onclick = function(e) {
            shareScreenBackground.style.display = 'none';
            shareScreen.style.display = 'none';
            shareScreenStatus = 'off';
        }

        window.onkeyup = function(e) {
            if (e.keyCode === 27) {
                if (shareScreenStatus == 'on') {
                    shareScreenBackground.style.display = 'none';
                    shareScreen.style.display = 'none';
                    shareScreenStatus = 'off';
                }
            }
        }

        btnTelegram.onclick = function(e) {
            shareScreenBackground.style.display = 'none';
            shareScreen.style.display = 'none';
            shareScreenStatus = 'off';
//            window.open('https://telegram.me/share/url?url=' + link + '');
            window.open('https://telegram.me/share/url?url=' + link + '','sharer','status=0,toolbar=0,width=650,height=500');
        }
        btnWhatsapp.onclick = function(e) {
            shareScreenBackground.style.display = 'none';
            shareScreen.style.display = 'none';
            shareScreenStatus = 'off';
            window.open('https://api.whatsapp.com/send?text=' + link );
        }
        btnFacebook.onclick = function(e) {
            shareScreenBackground.style.display = 'none';
            shareScreen.style.display = 'none';
            shareScreenStatus = 'off';
            window.open('https://www.facebook.com/sharer.php?u=' + link + '','sharer','status=0,toolbar=0,width=650,height=500');
        }
        btnVKontakte.onclick = function(e) {
            shareScreenBackground.style.display = 'none';
            shareScreen.style.display = 'none';
            shareScreenStatus = 'off';
            window.open('https://vkontakte.ru/share.php?url=' + link + '','sharer','status=0,toolbar=0,width=650,height=500');
        }
        btnOK.onclick = function(e) {
            shareScreenBackground.style.display = 'none';
            shareScreen.style.display = 'none';
            shareScreenStatus = 'off';
            window.open('https://ok.ru?st.cmd=addShare&st.s=1&st._surl=' + link + '&st.comments=OzeroNews','sharer','status=0,toolbar=0,width=650,height=500');
        }
        btnPinterest.onclick = function(e) {
            shareScreenBackground.style.display = 'none';
            shareScreen.style.display = 'none';
            shareScreenStatus = 'off';
            window.open('https://ru.pinterest.com/pin/create/button/?url=' + link + '','sharer','status=0,toolbar=0,width=650,height=500');
        }
        btnTwitter.onclick = function(e) {
            shareScreenBackground.style.display = 'none';
            shareScreen.style.display = 'none';
            shareScreenStatus = 'off';
            window.open('https://twitter.com/intent/tweet?text=Ozero News. Лента новостей. ' + link + '','sharer','status=0,toolbar=0,width=650,height=500');
        }
        btnMail.onclick = function(e) {
            shareScreenBackground.style.display = 'none';
            shareScreen.style.display = 'none';
            shareScreenStatus = 'off';
            window.open('https://connect.mail.ru/share?url=' + link + '','sharer','status=0,toolbar=0,width=650,height=500');
        }
        btnLinkedin.onclick = function(e) {
            shareScreenBackground.style.display = 'none';
            shareScreen.style.display = 'none';
            shareScreenStatus = 'off';
            window.open('https://www.linkedin.com/sharing/share-offsite/?url=' + link + '','sharer','toolbar=0,status=0,width=620,height=390');
        }

    }
})


//function shareTelegram() {
//    var baseURL = null;
//    console.log('share Telegram()');
//    window.open('https://telegram.me/share/url?url=' + baseURL + '','sharer','status=0,toolbar=0,width=650,height=500');
//}



function shareArticle() {
    var shareScreen = document.getElementById('btnShare_');

    document.querySelector('.share').classList.toggle('active');

}


// Button scroll up
if (document.querySelector('.scroll-up-btn') !== null) {

    let btnGoTop = document.querySelector('.scroll-up-btn');

    window.onscroll = function () {
        if (window.pageYOffset > document.documentElement.clientHeight) {
            btnGoTop.style.opacity = '0.7';
        } else {
            btnGoTop.style.opacity = '0.0';
        }
    }

    // плавный скролл наверх
    btnGoTop.addEventListener('click', function () {
        window.scrollBy({
            top: -document.documentElement.scrollHeight,
            behavior: 'smooth'
        });
    });
}

// Menu Profile
if (document.querySelector('.menu-profile-btn-burger') !== null) {
    document.querySelector('.menu-profile-btn-burger').onclick = function(e) {
        e.preventDefault;
        document.querySelector('.menu-profile-box').classList.toggle('active');
        document.querySelector('.menu-profile-btn-burger-line').classList.toggle('active');
    }
}

// Menu Admin
if (document.querySelector('.menu-admin-btn-burger') !== null) {
    document.querySelector('.menu-admin-btn-burger').onclick = function(e) {
        e.preventDefault;
        document.querySelector('.menu-admin-box').classList.toggle('active');
        document.querySelector('.menu-admin-btn-burger-line').classList.toggle('active');
    }
}


// Menu Resources
if (document.getElementById('btnResources') != null) {
    document.getElementById('btnResources').onclick = function(e) {
        e.preventDefault;
        if (document.querySelector('.menu-news-rubrics.active') != null) {
            document.querySelector('.menu-news-rubrics').classList.toggle('active');
        }
        document.querySelector('.menu-news-resources').classList.toggle('active');
        return false;
    }
}

// Menu Rubrics
if (document.getElementById('btnRubrics') != null) {
    document.getElementById('btnRubrics').onclick = function(e) {
        e.preventDefault;
        if (document.querySelector('.menu-news-resources.active') != null) {
            document.querySelector('.menu-news-resources').classList.toggle('active');
        }
        document.querySelector('.menu-news-rubrics').classList.toggle('active');
        return false;
    }
}

// Menu About
if (document.querySelector('.menu-about-btn-burger') !== null) {
    document.querySelector('.menu-about-btn-burger').onclick = function(e) {
        e.preventDefault;
        document.querySelector('.menu-about-box').classList.toggle('active');
        document.querySelector('.menu-about-btn-burger-line').classList.toggle('active');
    }
}


// Login
if (document.getElementById('btnLogin') != null) {
    document.getElementById('btnLogin').disabled = 'disabled';
    function checkFormLogin() {
        var email = document.getElementById('email').value.length;
        var password = document.getElementById('password').value.length;
        if (email > 0 && password > 0) {
            document.getElementById('btnLogin').disabled = false;
        }
    }
}

// Upload picture User Edit
if (document.getElementById('profileFileUpload') != null) {
    const inptUploadFile = document.getElementById('profileFileUpload');

    inptUploadFile.onchange = function() {
        if(this.files[0].size > 204800){
           alert("Загружаемый файл слишком большой. Выберите файл размером не более 200Kb");
           this.value = "";
        };
    };

    function previewProfileImage( uploader ) {
        //ensure a file was selected
        if (uploader.files && uploader.files[0]) {
            var imageFile = uploader.files[0];
            var reader = new FileReader();
            reader.onload = function (e) {
                //set the image data as source
                document.getElementById('profileImg').src = e.target.result;
//                $('#profileImage').attr('src', e.target.result);
            }
            reader.readAsDataURL( imageFile );
        }
    }

    $("#profileFileUpload").change(function(){
        previewProfileImage( this );
    });
}

// Upload picture User Registration
if (document.getElementById('registrationFileUpload') != null) {
    const inptUploadFile = document.getElementById('registrationFileUpload');

    inptUploadFile.onchange = function() {
            if(this.files[0].size > 204800){
               alert("Загружаемый файл слишком большой. Выберите файл размером не более 200Kb");
               this.value = "";
            };
        };

    function previewProfileImage( uploader ) {
        //ensure a file was selected
        if (uploader.files && uploader.files[0]) {
            var imageFile = uploader.files[0];
            var reader = new FileReader();
            reader.onload = function (e) {
                //set the image data as source
                document.getElementById('registrationImage').src = e.target.result;
//                $('#registrationImage').attr('src', e.target.result);
            }
            reader.readAsDataURL( imageFile );
        }
    }

    $("#registrationFileUpload").change(function(){
        previewProfileImage( this );
    });
}

// Registration user
if (document.getElementById('btnRegistration') != null) {
    document.getElementById('btnRegistration').disabled = 'disabled';
    function checkFormRegistration() {
        var email = document.getElementById('email').value.length;
        var username = document.getElementById('username').value.length;
        var password = document.getElementById('password').value.length;
        var password2 = document.getElementById('password2').value.length;
        var recaptcha = grecaptcha.getResponse().length;
        if (email > 0 && username > 0 && password > 0 && password2 > 0 && recaptcha > 0) {
            document.getElementById('btnRegistration').disabled = false;
        }
    }
    var onSubmit = function(response) {
        var email = document.getElementById('email').value.length;
        var username = document.getElementById('username').value.length;
        var password = document.getElementById('password').value.length;
        var password2 = document.getElementById('password2').value.length;
        var recaptcha = grecaptcha.getResponse().length;
        if (email > 0 && username > 0 && password > 0 && password2 > 0 && recaptcha > 0) {
            document.getElementById('btnRegistration').disabled = false;
        }
    }
}

// Profile edit
if (document.getElementById('btnEditSave') != null) {
    document.getElementById('btnEditSave').disabled = 'disabled';
    function checkFormEdit() {
        var username = document.getElementById('username').value.length;
        var password = document.getElementById('password').value.length;
        if (username > 0 && password > 0) {
            document.getElementById('btnEditSave').disabled = false;
        }
    }
}

// Delete User
if (document.getElementById('btnDeleteUser') != null) {
    document.getElementById('btnDeleteUser').disabled = 'disabled';
    function checkFormDeleteUser() {
        var password = document.getElementById('password').value.length;
        if (password > 0) {
            document.getElementById('btnDeleteUser').disabled = false;
        }
    }
}

// Change password
if (document.getElementById('btnChangePassword') != null) {
    document.getElementById('btnChangePassword').disabled = 'disabled';
    function checkFormChangePassword() {
        var currentPassword = document.getElementById('password').value.length;
        var newPassword = document.getElementById('newPassword').value.length;
        var newPassword2 = document.getElementById('newPassword2').value.length;
        if (currentPassword > 0 && newPassword > 0 && newPassword2 > 0) {
            document.getElementById('btnChangePassword').disabled = false;
        }
    }
}

// Recovery
if (document.getElementById('btnRecovery') != null) {
    document.getElementById('btnRecovery').disabled = 'disabled';
    function checkFormRecovery() {
        var email = document.getElementById('email').value.length;
        var recaptcha = grecaptcha.getResponse().length;
        if (email > 0 && recaptcha > 0) {
            document.getElementById('btnRecovery').disabled = false;
        }
    }
    var onSubmit = function(response) {
        var email = document.getElementById('email').value.length;
        var recaptcha = grecaptcha.getResponse().length;
        if (email > 0 && recaptcha > 0) {
            document.getElementById('btnRecovery').disabled = false;
        }
    }
}

// New Password
if (document.getElementById('btnNewPassword') != null) {
    document.getElementById('btnNewPassword').disabled = 'disabled';
    function checkFormNewPassword() {
        var password = document.getElementById('password').value.length;
        var password2 = document.getElementById('password2').value.length;
        if (password > 0 && password2 > 0) {
            document.getElementById('btnNewPassword').disabled = false;
        }
    }
}

// Contact page
if (document.getElementById('btnContact') != null) {
    document.getElementById('btnContact').disabled = 'disabled';
    function checkform() {
        var email = document.getElementById('emailContact').value.length;
        var subject = document.getElementById('subjectContact').value.length;
        var name = document.getElementById('nameContact').value.length;
        var message = document.getElementById('textContact').value.length;
        var recaptcha = grecaptcha.getResponse().length;
        if (email > 0 && subject > 0 && name > 0 && message > 0 && recaptcha > 0) {
            document.getElementById('btnContact').disabled = false;
        }
    }
    var onSubmit = function(response) {
        var email = document.getElementById('emailContact').value.length;
        var subject = document.getElementById('subjectContact').value.length;
        var name = document.getElementById('nameContact').value.length;
        var message = document.getElementById('textContact').value.length;
        var recaptcha = grecaptcha.getResponse().length;
        if (email > 0 && subject > 0 && name > 0 && message > 0 &&  recaptcha > 0) {
            document.getElementById('btnContact').disabled = false;
        }
    }

}




