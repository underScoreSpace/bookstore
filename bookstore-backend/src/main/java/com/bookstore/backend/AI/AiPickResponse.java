package com.bookstore.backend.AI;

import java.util.List;

public class AiPickResponse {
    public List<Pick> selected;

    public static class Pick {
        public Integer id;
        public String reason;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pick other)) return false;
            return id != null && id.equals(other.id);
        }

        @Override
        public int hashCode() {
            return id == null ? 0 : id.hashCode();
        }
    }
}
