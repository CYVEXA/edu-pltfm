document.addEventListener('DOMContentLoaded', () => {
    loadCourses();
});

async function loadCourses() {
    const courseGrid = document.getElementById('courseGrid');
    
    // Default courses if API is empty or failing
    const defaultCourses = [
        {
            title: "Cybersecurity Basics",
            description: "Learn the fundamentals of protecting systems and networks.",
            price: "Free",
            thumbnailUri: "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&q=80&w=400"
        },
        {
            title: "Ethical Hacking",
            description: "Master the art of penetration testing and security auditing.",
            price: "Free",
            thumbnailUri: "https://images.unsplash.com/photo-1563986768609-322da13575f3?auto=format&fit=crop&q=80&w=400"
        },
        {
            title: "Web Development",
            description: "Build modern, responsive websites with HTML, CSS, and JS.",
            price: "Free",
            thumbnailUri: "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&q=80&w=400"
        },
        {
            title: "Workshop Recordings",
            description: "Access our exclusive archive of past technical workshops.",
            price: "Free",
            thumbnailUri: "https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?auto=format&fit=crop&q=80&w=400"
        }
    ];

    try {
        const response = await fetch('/api/courses');
        const courses = await response.json();
        
        if (courses && courses.length > 0) {
            renderCourses(courses);
        } else {
            renderCourses(defaultCourses);
        }
    } catch (error) {
        console.error('Error fetching courses:', error);
        renderCourses(defaultCourses);
    }
}

function renderCourses(courses) {
    const courseGrid = document.getElementById('courseGrid');
    courseGrid.innerHTML = '';

    courses.forEach(course => {
        const card = document.createElement('div');
        card.className = 'soft-card course-card';
        card.innerHTML = `
            <img src="${course.thumbnailUri || 'https://via.placeholder.com/400x200'}" alt="${course.title}" class="course-thumb">
            <div class="course-info">
                <h3 class="course-title">${course.title}</h3>
                <p class="course-desc">${course.description}</p>
            </div>
            <div class="course-footer">
                <span class="price">${course.type || 'Free'}</span>
                <button class="soft-button" onclick="viewDetails(${course.id || 0})">View Details</button>
            </div>
        `;
        courseGrid.appendChild(card);
    });
}

function viewDetails(id) {
    if (!id) {
        alert('Course details coming soon!');
        return;
    }
    window.location.href = `/course-details.html?id=${id}`;
}
