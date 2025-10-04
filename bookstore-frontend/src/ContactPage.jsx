import "./ContactPage.css"

export default function ContactPage() {
    return (
        <div className="contact-page">
            <h2>Contact Us</h2>
            <p>Have a question?</p>
                <form className="info-table">
                    <div>
                        <label>Name</label>
                        <input
                            id="name"
                            name="name"
                            required
                        />
                    </div>

                    <div>
                        <label>Email</label>
                        <input
                            id="email"
                            name="email"
                            type="email"
                            required
                        />
                    </div>

                    <div>
                        <label>Message</label>
                        <textarea
                            id="message"
                            name="message"
                            rows="5"
                            required
                        />
                    </div>
                    <button>Send Message</button>
                </form>
        </div>
    );
}
